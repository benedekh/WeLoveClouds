package weloveclouds.ecs.models.tasks;

import java.util.ArrayList;
import java.util.List;

import static weloveclouds.ecs.models.tasks.Status.*;

/**
 * Created by Benoit on 2016-11-19.
 */
public class BatchOfRetryableTasks extends AbstractBatchOfTasks<AbstractRetryableTask> {

    public BatchOfRetryableTasks(BatchPurpose batchPurpose) {
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
