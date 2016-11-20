package weloveclouds.ecs.services;

import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.IBatchTasks;

/**
 * Created by Benoit on 2016-11-19.
 */
public interface ITaskService {
    void launchTask(AbstractRetryableTask task);

    void launchBatchTasks(IBatchTasks<AbstractRetryableTask> batchTasks);
}
