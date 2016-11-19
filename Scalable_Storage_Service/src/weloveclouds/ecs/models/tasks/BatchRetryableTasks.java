package weloveclouds.ecs.models.tasks;

import java.util.List;
import java.util.Set;

/**
 * Created by Benoit on 2016-11-19.
 */
public class BatchRetryableTasks implements IBatchTasks<AbstractRetryableTask> {
    @Override
    public void addTask(AbstractRetryableTask task) {

    }

    @Override
    public void addTasks(List<AbstractRetryableTask> tasks) {

    }

    @Override
    public Set<AbstractRetryableTask> getTasks() {
        return null;
    }

    @Override
    public Set<AbstractRetryableTask> getFailedTasks() {
        return null;
    }

    @Override
    public Set<AbstractRetryableTask> getSucceededTasks() {
        return null;
    }
}
