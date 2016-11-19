package weloveclouds.ecs.models.tasks;

import java.util.List;
import java.util.Set;

/**
 * Created by Benoit on 2016-11-19.
 */
public interface IBatchTasks {
    void addTask(AbstractTask task);

    void addTasks(List<AbstractTask> tasks);

    Set<AbstractTask> getTasks();

    Set<AbstractTask> getFailedTasks();

    Set<AbstractTask> getSucceededTasks();
}
