package weloveclouds.ecs.models.tasks;

import java.util.List;
import java.util.Set;

/**
 * Created by Benoit on 2016-11-19.
 */
public interface IBatchTasks<T> {
    void addTask(T task);

    void addTasks(List<T> tasks);

    Set<T> getTasks();

    Set<T> getFailedTasks();

    Set<T> getSucceededTasks();
}
