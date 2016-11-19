package weloveclouds.ecs.services;

import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.IBatchTasks;

/**
 * Created by Benoit on 2016-11-19.
 */
public class TaskService implements ITaskService {
    @Override
    public void launchTask(AbstractRetryableTask task) {

    }

    @Override
    public void launchBatchTasks(IBatchTasks batchTasks) {

    }
}
