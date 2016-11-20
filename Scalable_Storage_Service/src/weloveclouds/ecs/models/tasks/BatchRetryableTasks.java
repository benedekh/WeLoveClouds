package weloveclouds.ecs.models.tasks;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static weloveclouds.ecs.models.tasks.Status.*;

/**
 * Created by Benoit on 2016-11-19.
 */
public class BatchRetryableTasks implements IBatchTasks<AbstractRetryableTask> {
    private String id;
    private Set<AbstractRetryableTask> tasks;

    public BatchRetryableTasks() {
        this.id = UUID.randomUUID().toString();
        this.tasks = new LinkedHashSet<>();
    }

    @Override
    public void addTask(AbstractRetryableTask task) {
        tasks.add(task);
        task.setBatchId(id);
    }

    @Override
    public void addTasks(List<AbstractRetryableTask> tasks) {
        for (AbstractRetryableTask task : tasks) {
            addTask(task);
        }
    }

    @Override
    public List<AbstractRetryableTask> getTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public List<AbstractRetryableTask> getFailedTasks() {
        return getTasksWithStatus(FAILED);
    }

    @Override
    public List<AbstractRetryableTask> getSucceededTasks() {
        return getTasksWithStatus(COMPLETED);
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
