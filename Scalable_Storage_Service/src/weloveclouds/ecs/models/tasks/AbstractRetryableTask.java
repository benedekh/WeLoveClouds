package weloveclouds.ecs.models.tasks;

import weloveclouds.ecs.client.Client;
import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.exceptions.task.RetryableException;
import weloveclouds.ecs.models.commands.AbstractCommand;

import static weloveclouds.ecs.models.tasks.Status.WAITING;

/**
 * Created by Benoit on 2016-11-18.
 */
public abstract class AbstractRetryableTask {
    protected Status status;
    protected AbstractCommand command;
    protected AbstractCommand successCommand;
    protected AbstractCommand failCommand;
    protected int numberOfAttempt;
    protected int maxNumberOfRetries;

    public AbstractRetryableTask(int maxNumberOfRetries, AbstractCommand command, AbstractCommand
            successCommand, AbstractCommand failCommand) {
        this.status = WAITING;
        this.command = command;
        this.successCommand = successCommand;
        this.failCommand = failCommand;
        this.numberOfAttempt = 0;
        this.maxNumberOfRetries = maxNumberOfRetries;
    }

    public int getMaximumNumberOfRetries() {
        return this.maxNumberOfRetries;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public AbstractCommand getCommand() {
        return command;
    }

    public AbstractCommand getSuccessCommand() {
        return successCommand;
    }

    public AbstractCommand getFailCommand() {
        return failCommand;
    }

    public abstract void runCommand() throws ClientSideException;

    public abstract void runSuccessCommand() throws ClientSideException;

    public abstract void runFailCommand() throws ClientSideException;

    public void run() throws ClientSideException, RetryableException {
        try {
            runCommand();
            runSuccessCommand();
        } catch (Exception e) {
            if (numberOfAttempt < maxNumberOfRetries) {
                numberOfAttempt++;
                throw new RetryableException("Retry attempt: " + numberOfAttempt + " on: " +
                        maxNumberOfRetries, e);
            } else {
                runFailCommand();
                throw new ClientSideException("Maximum number of attempt reached, will not " +
                        "retry", e);
            }
        }
    }
}

