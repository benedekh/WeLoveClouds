package weloveclouds.ecs.services;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import weloveclouds.commons.monitoring.models.Metric;
import weloveclouds.commons.monitoring.statsd.IStatsdClient;
import weloveclouds.commons.monitoring.statsd.StatsdClientFactory;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.AbstractBatchOfTasks;
import weloveclouds.ecs.workers.TaskWorker;

import static weloveclouds.commons.monitoring.models.Service.ECS;
import static weloveclouds.commons.monitoring.statsd.IStatsdClient.SINGLE_EVENT;
import static weloveclouds.ecs.models.tasks.Status.RUNNING;
import static weloveclouds.ecs.workers.WorkerStatus.ERROR;

public class TaskService implements ITaskService {
    private static final Logger LOGGER = Logger.getLogger(TaskService.class);
    private static final IStatsdClient STATSD_CLIENT = StatsdClientFactory
            .createStatdClientFromEnvironment();

    private Map<String, AbstractRetryableTask> runningTasks;
    private Map<String, AbstractRetryableTask> failedTasks;
    private Map<String, AbstractRetryableTask> succeededTasks;
    private Map<String, AbstractBatchOfTasks<AbstractRetryableTask>> batch;

    public TaskService() {
        this.runningTasks = new LinkedHashMap<>();
        this.failedTasks = new LinkedHashMap<>();
        this.succeededTasks = new LinkedHashMap<>();
        this.batch = new LinkedHashMap<>();
    }

    @Override
    public void launchTask(AbstractRetryableTask task) {
        runningTasks.put(task.getId(), task);
        TaskWorker taskWorker = new TaskWorker(task);
        taskWorker.addObserver(this);
        new Thread(taskWorker).start();

        STATSD_CLIENT.recordGaugeValue(new Metric.Builder().service(ECS).name(Arrays.asList
                ("tasks", "running")).build(), runningTasks.size());
        STATSD_CLIENT.incrementCounter(new Metric.Builder().service(ECS).name(Arrays.asList
                ("tasks", "launched")).build(), SINGLE_EVENT);
    }

    @Override
    public void launchBatch(AbstractBatchOfTasks<AbstractRetryableTask> batchTasks) {
        batch.put(batchTasks.getId(), batchTasks);

        for (AbstractRetryableTask task : batchTasks.getTasks()) {
            launchTask(task);
            task.setStatus(RUNNING);
        }

        STATSD_CLIENT.incrementCounter(new Metric.Builder().service(ECS).name(Arrays.asList
                ("batches", "launched")).build(), SINGLE_EVENT);
    }

    @Override
    synchronized public void update(Observable obs, Object id) {
        TaskWorker worker = (TaskWorker) obs;
        String taskId = (String) id;
        AbstractRetryableTask task = runningTasks.remove(taskId);

        STATSD_CLIENT.recordExecutionTime(new Metric.Builder().service(ECS).name(Arrays.asList
                ("tasks", "exec_time")).build(), new Duration(task.getStartTime(), Instant.now()));

        if (worker.getStatus() == ERROR) {
            failedTasks.put(taskId, task);
            STATSD_CLIENT.incrementCounter(new Metric.Builder().service(ECS).name(Arrays.asList
                    ("tasks", "failed")).build());
            LOGGER.warn(task.toString());
        } else {
            succeededTasks.put(taskId, task);
            STATSD_CLIENT.incrementCounter(new Metric.Builder().service(ECS).name(Arrays.asList
                    ("tasks", "succeeded")).build());
            LOGGER.info(task.toString());
        }

        STATSD_CLIENT.recordGaugeValue(new Metric.Builder().service(ECS).name(Arrays.asList
                ("tasks", "running")).build(), runningTasks.size());

        if (task.isBatched()) {
            batch.get(task.getBatchId()).taskExecutionFinishedCallback();
        }
    }
}
