package weloveclouds.ecs.models.tasks;

import java.util.List;
import java.util.Set;

/**
 * Created by Benoit on 2016-11-19.
 */
public interface IBatchTasks<T> {
    void addTask(T task);

    void addTasks(List<T> tasks);

    String getId();

    List<T> getTasks();

    List<T> getFailedTasks();

    List<T> getSucceededTasks();
}
