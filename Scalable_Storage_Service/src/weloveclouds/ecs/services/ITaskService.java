package weloveclouds.ecs.services;

import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.AbstractTask;
import weloveclouds.ecs.models.tasks.IBatchTasks;

/**
 * Created by Benoit on 2016-11-19.
 */
public interface ITaskService {
    <T extends AbstractTask> void launchTask(T task);

    <T extends AbstractTask> void launchBatchTasks(IBatchTasks<T> batchTasks);
}
