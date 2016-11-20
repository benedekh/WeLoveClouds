package weloveclouds.ecs.models.tasks;

import weloveclouds.ecs.client.Client;
import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.exceptions.task.RetryableException;

import static weloveclouds.ecs.models.tasks.Status.WAITING;

/**
 * Created by Benoit on 2016-11-18.
 */
public abstract class AbstractRetryableTask<T1, T2, T3> {
    protected Status status;
    protected T1 command;
    protected T2 successCommand;
    protected T3 failCommand;
    protected int numberOfAttempt;
    protected int maxNumberOfRetries;

    public AbstractRetryableTask(int maxNumberOfRetries, T1 command, T2 successCommand, T3 failCommand) {
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

    public T1 getCommand() {
        return command;
    }

    public T2 getSuccessCommand() {
        return successCommand;
    }

    public T3 getFailCommand() {
        return failCommand;
    }

    public abstract void runCommand() throws ClientSideException;

    public abstract void runSuccessCommand() throws ClientSideException;

    public abstract void runFailCommand() throws ClientSideException;

    public void run() throws ClientSideException, RetryableException {
        try {
            runCommand();
            runSuccessCommand();
        } catch (ClientSideException e) {
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

