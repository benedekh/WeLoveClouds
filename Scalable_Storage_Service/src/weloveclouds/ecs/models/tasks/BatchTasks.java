package weloveclouds.ecs.models.tasks;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Benoit on 2016-11-19.
 */
public class BatchTasks implements IBatchTasks<AbstractTask> {
    Set<AbstractTask> tasks;
    Set<AbstractTask> failedTasks;
    Set<AbstractTask> succeededTasks;

    public BatchTasks() {
        this.tasks = new LinkedHashSet<>();
        this.failedTasks = new LinkedHashSet<>();
        this.succeededTasks = new LinkedHashSet<>();
    }

    public BatchTasks(List<AbstractTask> tasks) {
        this.tasks = new LinkedHashSet<>(tasks);
        this.failedTasks = new LinkedHashSet<>();
        this.succeededTasks = new LinkedHashSet<>();
    }

    @Override
    public void addTask(AbstractTask task) {
        tasks.add(task);
    }

    @Override
    public void addTasks(List<AbstractTask> tasks) {
        this.tasks.addAll(tasks);
    }

    @Override
    public Set<AbstractTask> getTasks() {
        return tasks;
    }

    @Override
    public Set<AbstractTask> getFailedTasks() {
        return failedTasks;
    }

    @Override
    public Set<AbstractTask> getSucceededTasks() {
        return succeededTasks;
    }
}
