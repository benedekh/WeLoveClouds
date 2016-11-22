package weloveclouds.ecs.models.tasks;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.UUID;

import static weloveclouds.ecs.models.tasks.Status.*;

/**
 * Created by Benoit on 2016-11-19.
 */
public class BatchRetryableTasks extends AbstractBatchTasks<AbstractRetryableTask> {

    public BatchRetryableTasks(BatchPurpose batchPurpose) {
        super(batchPurpose);
    }

    private int completedTask = 0;

    @Override
    public void addTask(AbstractRetryableTask task) {
        this.tasks.add(task);
        task.setBatchId(id);
    }

    @Override
    public List<AbstractRetryableTask> getFailedTasks() {
        return getTasksWithStatus(FAILED);
    }

    @Override
    public List<AbstractRetryableTask> getSucceededTasks() {
        return getTasksWithStatus(COMPLETED);
    }

    @Override
    synchronized public void taskExecutionFinishedCallback() {
        if (isBatchExecutionComplete()) {
            setChanged();
            notifyObservers(getFailedTasks());
        }
    }

    private boolean isBatchExecutionComplete() {
        return ++completedTask == tasks.size();
    }

    private List<AbstractRetryableTask> getTasksWithStatus(Status status) {
        List<AbstractRetryableTask> requestedTask = new ArrayList<>();

        for (AbstractRetryableTask task : tasks) {
            if (task.getStatus() == status) {
                requestedTask.add(task);
            }
        }
        return requestedTask;
    }
}
