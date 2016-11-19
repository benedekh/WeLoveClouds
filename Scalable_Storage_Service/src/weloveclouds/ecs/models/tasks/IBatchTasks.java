package weloveclouds.ecs.models.tasks;

import java.util.List;

/**
 * Created by Benoit on 2016-11-19.
 */
public interface IBatchTasks {
    void addTask(AbstractRetryableTask task);

    void addTasks(List<AbstractRetryableTask> tasks);

    List<AbstractRetryableTask> getTasks();

    List<AbstractRetryableTask> getFailedTasks();

    List<AbstractRetryableTask> getSucceededTasks();
}
