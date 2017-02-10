package weloveclouds.ecs.models.tasks;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.UUID;

import weloveclouds.commons.utils.StringUtils;

/**
 * Created by Benoit on 2016-11-19.
 */
public abstract class AbstractBatchOfTasks<T> extends Observable {
    protected BatchPurpose batchPurpose;
    protected String id;
    protected Set<T> tasks;

    public AbstractBatchOfTasks(BatchPurpose batchPurpose) {
        this.batchPurpose = batchPurpose;
        this.id = UUID.randomUUID().toString();
        this.tasks = new LinkedHashSet<>();
    }

    public BatchPurpose getPurpose() {
        return batchPurpose;
    }

    public boolean hasFailed() {
        return getFailedTasks().containsAll(getTasks());
    }

    public abstract void addTask(T task);

    public void addTasks(List<T> tasks) {
        for (T task : tasks) {
            addTask(task);
        }
    }

    public String getId() {
        return id;
    }

    public List<T> getTasks() {
        return new ArrayList<T>(tasks);
    }

    public abstract List<T> getFailedTasks();

    public abstract List<T> getSucceededTasks();

    public abstract void taskExecutionFinishedCallback();

    public String toString() {
        return StringUtils.join(" ", "Batch id:", id, "Purpose:", batchPurpose);
    }
}
