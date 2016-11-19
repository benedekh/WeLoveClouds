package weloveclouds.ecs.workers;

import java.util.Observable;

import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.exceptions.task.RetryableException;
import weloveclouds.ecs.models.tasks.AbstractTask;
import weloveclouds.ecs.models.tasks.Status;
import weloveclouds.ecs.models.tasks.WorkerStatus;

import static weloveclouds.ecs.models.tasks.Status.*;
import static weloveclouds.ecs.models.tasks.WorkerStatus.*;

/**
 * Created by Benoit on 2016-11-18.
 */
public class TaskWorker<T extends AbstractTask> extends Observable implements Runnable {
    private T task;
    private WorkerStatus status;

    public TaskWorker(T task) {
        this.task = task;
    }

    public T getTask() {
        return this.task;
    }

    public Status getTaskStatus() {
        return task.getStatus();
    }

    public void run() {
        status = RUNNING;
        while (task.getStatus() != COMPLETED && task.getStatus() != FAILED) {
            try {
                task.run();
            } catch (RetryableException e) {
                //LOG e.getMessage(); + cause
            } catch (ClientSideException e) {
                task.setStatus(FAILED);
            }
        }
        status = task.getStatus() == COMPLETED ? FINISHED : ERROR;
        setChanged();
        notifyObservers();
    }
}
