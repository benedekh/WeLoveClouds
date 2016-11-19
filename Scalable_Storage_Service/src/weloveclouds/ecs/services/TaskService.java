package weloveclouds.ecs.services;

import java.util.Observable;
import java.util.Observer;

import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.AbstractTask;
import weloveclouds.ecs.models.tasks.IBatchTasks;
import weloveclouds.ecs.workers.TaskWorker;

/**
 * Created by Benoit on 2016-11-19.
 */
public class TaskService implements ITaskService, Observer {

    @Override
    public <T extends AbstractTask> void launchTask(T task) {

    }

    @Override
    public <T extends AbstractTask> void launchBatchTasks(IBatchTasks<T> batchTasks) {
        for (T task : batchTasks.getTasks()) {
            TaskWorker<T> taskWorker = new TaskWorker<>(task);
            taskWorker.addObserver(this);
            new Thread(taskWorker).start();
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
