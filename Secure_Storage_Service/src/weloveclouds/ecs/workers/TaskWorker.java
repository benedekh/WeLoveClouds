package weloveclouds.ecs.workers;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import java.util.Observable;

import weloveclouds.commons.retryer.ExponentialBackoffIntervalComputer;
import weloveclouds.commons.retryer.IBackoffIntervalComputer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.ecs.exceptions.task.RetryableException;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;

import static weloveclouds.ecs.models.tasks.Status.*;
import static weloveclouds.ecs.workers.WorkerStatus.*;
import static weloveclouds.ecs.workers.WorkerStatus.WAITING;
import static weloveclouds.ecs.workers.WorkerStatus.RUNNING;

/**
 * Created by Benoit on 2016-11-18.
 */
public class TaskWorker extends Observable implements Runnable {
    private IBackoffIntervalComputer backoffIntervalComputer;
    private AbstractRetryableTask task;
    private WorkerStatus status;
    private final Logger logger;

    public TaskWorker(AbstractRetryableTask task) {
        this.logger = Logger.getLogger(getClass());
        this.backoffIntervalComputer = new ExponentialBackoffIntervalComputer(new Duration(300));
        this.task = task;
        status = WAITING;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public AbstractRetryableTask getTask() {
        return this.task;
    }

    public void run() {
        try {
            status = RUNNING;
            while (!task.isCompleted() && !task.hasReachedMaximumNumberOfRetries()) {
                try {
                    task.run();
                } catch (RetryableException e) {
                    logger.info(StringUtils.join(" ",
                            "Task id:", task.getId(), "Retry attempt:", task.getNumberOfRetries(),
                            "on:", task.getMaximumNumberOfRetries(),
                            "will retry with cause:", e.getMessage()), e);
                    task.incrementNumberOfRetries();
                    Thread.sleep(backoffIntervalComputer.computeIntervalFrom(
                            task.getNumberOfRetries()).getMillis());
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                    task.setStatus(FAILED);
                    task.setNumberOfRetriesToMaximumNumberOfRetries();
                }
            }
            if (task.isCompleted()) {
                status = FINISHED;
            } else {
                status = ERROR;
                logger.warn(StringUtils.join(" ", "Task id:", task.getId(), "Will not be retry, maximum " +
                        "number of retries reached."));
            }
            setChanged();
            notifyObservers(task.getId());
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
}
