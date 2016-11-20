package weloveclouds.ecs.workers;

import java.util.Observable;

import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.exceptions.task.RetryableException;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.Status;
import weloveclouds.ecs.models.tasks.WorkerStatus;

import static weloveclouds.ecs.models.tasks.Status.*;
import static weloveclouds.ecs.models.tasks.WorkerStatus.*;
import static weloveclouds.ecs.models.tasks.WorkerStatus.WAITING;

/**
 * Created by Benoit on 2016-11-18.
 */
public class TaskWorker extends Observable implements Runnable {
    private AbstractRetryableTask task;
    private WorkerStatus status;

    public TaskWorker(AbstractRetryableTask task) {
        this.task = task;
        status = WAITING;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public AbstractRetryableTask getTask() {
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
