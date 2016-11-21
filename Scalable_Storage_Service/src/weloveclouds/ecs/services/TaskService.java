package weloveclouds.ecs.services;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.IBatchTasks;
import weloveclouds.ecs.workers.TaskWorker;

import static weloveclouds.ecs.workers.WorkerStatus.ERROR;

/**
 * Created by Benoit on 2016-11-19.
 */
public class TaskService implements ITaskService, Observer {
    Map<String, AbstractRetryableTask> runningTasks;
    Map<String, AbstractRetryableTask> failedTasks;
    Map<String, AbstractRetryableTask> succeededTasks;
    Map<String, IBatchTasks<AbstractRetryableTask>> batch;

    public TaskService() {
        failedTasks = new LinkedHashMap<>();
        succeededTasks = new LinkedHashMap<>();
    }

    @Override
    public void launchTask(AbstractRetryableTask task) {
        runningTasks.put(task.getId(), task);
        TaskWorker taskWorker = new TaskWorker(task);
        taskWorker.addObserver(this);
        new Thread(taskWorker).start();
    }

    @Override
    public void launchBatchTasks(IBatchTasks<AbstractRetryableTask> batchTasks) {
        batch.put(batchTasks.getId(), batchTasks);

        for (AbstractRetryableTask task : batchTasks.getTasks()) {
            launchTask(task);
        }
    }

    @Override
    synchronized public void update(Observable obs, Object id) {
        TaskWorker worker = (TaskWorker) obs;
        String taskId = (String) id;
        AbstractRetryableTask task = runningTasks.remove(taskId);

        if (worker.getStatus() == ERROR) {
            failedTasks.put(taskId, task);
        } else {
            succeededTasks.put(taskId, task);
            //LOG error
        }
    }
}