package weloveclouds.ecs.workers;

import org.apache.log4j.Logger;

import java.util.Observable;

import weloveclouds.ecs.exceptions.task.RetryableException;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;
import weloveclouds.ecs.models.tasks.Status;

import static weloveclouds.ecs.models.tasks.Status.*;
import static weloveclouds.ecs.workers.WorkerStatus.*;
import static weloveclouds.ecs.workers.WorkerStatus.WAITING;
import static weloveclouds.ecs.workers.WorkerStatus.RUNNING;

/**
 * Created by Benoit on 2016-11-18.
 */
public class TaskWorker extends Observable implements Runnable {
    private AbstractRetryableTask task;
    private WorkerStatus status;
    private final Logger logger;

    public TaskWorker(AbstractRetryableTask task) {
        this.logger = Logger.getLogger(getClass());
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
        while (task.getStatus() != COMPLETED && !task.hasReachedMaximumNumberOfRetries()) {
            try {
                task.run();
            } catch (RetryableException e) {
                logger.info("Task id: " + task.getId() + " Retry attempt: " + task.getNumberOfRetries() +
                        " on: " + task.getMaximumNumberOfRetries() + " will retry with cause: " + e.getMessage(), e);
                task.incrementNumberOfRetries();
            } catch (Exception e) {
                logger.info(e.getMessage());
                task.setStatus(FAILED);
                task.setNumberOfRetriesToMaximumNumberOfRetries();
            }
        }
        if (task.getStatus() == COMPLETED) {
            status = FINISHED;
        } else {
            status = ERROR;
            logger.warn("Task id: " + task.getId() + " Will not be retry, maximum number of " +
                    "retries reached");
        }
        setChanged();
        notifyObservers(task.getId());
    }
}
