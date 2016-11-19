package weloveclouds.ecs.workers;

import java.util.Observable;

import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.models.tasks.AbstractRetryableTask;

import static weloveclouds.ecs.models.tasks.Status.*;

/**
 * Created by Benoit on 2016-11-18.
 */
public class TaskWorker extends Observable implements Runnable {
    private AbstractRetryableTask task;

    public TaskWorker(AbstractRetryableTask task) {
        this.task = task;
    }

    public void run() {
        while (task.getStatus() != COMPLETED) {
            try {
                task.run();
            } catch (ClientSideException e) {

            }
        }
    }
}
