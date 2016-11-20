package weloveclouds.ecs.services;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.IBatchTasks;
import weloveclouds.ecs.workers.TaskWorker;

import static weloveclouds.ecs.workers.WorkerStatus.ERROR;

/**
 * Created by Benoit on 2016-11-19.
 */
public class TaskService implements ITaskService, Observer {
    Map<String, AbstractRetryableTask> runningTasks;
    Set<AbstractRetryableTask> failedTasks;
    Set<AbstractRetryableTask> succeededTasks;

    public TaskService() {
        failedTasks = new LinkedHashSet<>();
        succeededTasks = new LinkedHashSet<>();
    }

    @Override
    public void launchTask(AbstractRetryableTask task) {

    }

    @Override
    public void launchBatchTasks(IBatchTasks<AbstractRetryableTask> batchTasks) {
        for (AbstractRetryableTask task : batchTasks.getTasks()) {
            runningTasks.put(task.getId(), task);
            TaskWorker taskWorker = new TaskWorker(task);
            taskWorker.addObserver(this);
            new Thread(taskWorker).start();
        }
    }

    @Override
    synchronized public void update(Observable obs, Object arg) {
        TaskWorker worker = (TaskWorker) obs;
        if (worker.getStatus() == ERROR) {
            failedTasks.add(worker.getTask());
        } else {
            succeededTasks.add(worker.getTask());
            //LOG error
        }
    }
}
