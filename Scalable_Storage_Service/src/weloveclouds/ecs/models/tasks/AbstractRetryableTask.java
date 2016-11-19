package weloveclouds.ecs.models.tasks;

import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.exceptions.task.RetryableException;
import weloveclouds.ecs.models.commands.ICommand;

/**
 * Created by Benoit on 2016-11-18.
 */
public abstract class AbstractRetryableTask extends AbstractTask {
    protected int numberOfAttempt;
    protected int maxNumberOfRetries;

    public AbstractRetryableTask(int maxNumberOfRetries, ICommand command, ICommand successCommand, ICommand
            failCommand) {
        super(command, successCommand, failCommand);
        this.numberOfAttempt = 0;
        this.maxNumberOfRetries = maxNumberOfRetries;
    }

    public int getMaximumNumberOfRetries() {
        return this.maxNumberOfRetries;
    }

    public abstract void run() throws ClientSideException, RetryableException;
}

