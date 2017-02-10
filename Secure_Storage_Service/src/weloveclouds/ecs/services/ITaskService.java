package weloveclouds.ecs.services;

import java.util.Observer;

import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.AbstractBatchOfTasks;

/**
 * Created by Benoit on 2016-11-19.
 */
public interface ITaskService extends Observer {
    void launchTask(AbstractRetryableTask task);

    void launchBatch(AbstractBatchOfTasks<AbstractRetryableTask> batchTasks);
}
