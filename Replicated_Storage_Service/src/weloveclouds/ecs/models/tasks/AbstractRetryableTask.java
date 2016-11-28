package weloveclouds.ecs.models.tasks;

import org.joda.time.Instant;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.exceptions.task.RetryableException;
import weloveclouds.ecs.models.commands.AbstractCommand;

import static weloveclouds.ecs.models.tasks.Status.COMPLETED;
import static weloveclouds.ecs.models.tasks.Status.FAILED;
import static weloveclouds.ecs.models.tasks.Status.WAITING;

/**
 * Created by Benoit on 2016-11-18.
 */
public abstract class AbstractRetryableTask {
    protected String batchId;
    protected String id;
    protected Status status;
    protected Instant startTime;
    protected AbstractCommand command;
    protected List<AbstractCommand> successCommands;
    protected List<AbstractCommand> failCommands;
    protected int numberOfRetries;
    protected int maxNumberOfRetries;

    public AbstractRetryableTask(int maxNumberOfRetries, AbstractCommand command,
                                 List<AbstractCommand> successCommand, List<AbstractCommand> failCommand) {
        this.id = UUID.randomUUID().toString();
        this.status = WAITING;
        this.command = command;
        this.successCommands = successCommand;
        this.failCommands = failCommand;
        this.numberOfRetries = 0;
        this.maxNumberOfRetries = maxNumberOfRetries;
    }

    public void setNumberOfRetriesToMaximumNumberOfRetries() {
        numberOfRetries = getMaximumNumberOfRetries();
    }

    public int getMaxNumberOfRetries() {
        return maxNumberOfRetries;
    }

    public void incrementNumberOfRetries() {
        numberOfRetries++;
    }

    public int getNumberOfRetries() {
        return numberOfRetries;
    }

    public boolean hasReachedMaximumNumberOfRetries() {
        return numberOfRetries >= maxNumberOfRetries;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
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

    public List<AbstractCommand> getSuccessCommands() {
        return successCommands;
    }

    public List<AbstractCommand> getFailCommands() {
        return failCommands;
    }

    public abstract void runCommand() throws ClientSideException;

    public abstract void runSuccessCommand() throws ClientSideException;

    public abstract void runFailCommand() throws ClientSideException;

    public void run() throws ClientSideException, RetryableException {
        try {
            startTime = Instant.now();
            runCommand();
            runSuccessCommand();
            status = COMPLETED;
        } catch (Exception e) {
            status = FAILED;
            runFailCommand();
            throw new RetryableException(e.getMessage(), e);
        }
    }

    public String toString() {
        return CustomStringJoiner.join(" ", Arrays.asList("Task id:", id, "Command:", command
                .toString(), "Status:", status.name()));
    }
}

