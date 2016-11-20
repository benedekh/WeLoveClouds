package weloveclouds.ecs.models.tasks;

import java.util.Arrays;
import java.util.UUID;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.exceptions.task.RetryableException;
import weloveclouds.ecs.models.commands.AbstractCommand;

import static weloveclouds.ecs.models.tasks.Status.WAITING;

/**
 * Created by Benoit on 2016-11-18.
 */
public abstract class AbstractRetryableTask {
    protected String batchId;
    protected String id;
    protected Status status;
    protected AbstractCommand command;
    protected AbstractCommand successCommand;
    protected AbstractCommand failCommand;
    protected int numberOfAttempt;
    protected int maxNumberOfRetries;

    public AbstractRetryableTask(int maxNumberOfRetries, AbstractCommand command, AbstractCommand
            successCommand, AbstractCommand failCommand) {
        this.id = UUID.randomUUID().toString();
        this.status = WAITING;
        this.command = command;
        this.successCommand = successCommand;
        this.failCommand = failCommand;
        this.numberOfAttempt = 0;
        this.maxNumberOfRetries = maxNumberOfRetries;
    }

    public String getId() {
        return this.id;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
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

    public String toString() {
        return CustomStringJoiner.join(" ", Arrays.asList("Task id:", id, "Command:", command
                .toString(), "Status:", status.name()));
    }
}

