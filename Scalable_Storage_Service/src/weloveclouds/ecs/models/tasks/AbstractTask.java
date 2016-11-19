package weloveclouds.ecs.models.tasks;

import static weloveclouds.ecs.models.tasks.Status.*;

import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.models.commands.ICommand;

/**
 * Created by Benoit on 2016-11-19.
 */
public abstract class AbstractTask {
    protected Status status;
    protected ICommand command;
    protected ICommand successCommand;
    protected ICommand failCommand;

    public AbstractTask(ICommand command, ICommand successCommand, ICommand failCommand) {
        this.status = WAITING;
        this.command = command;
        this.successCommand = successCommand;
        this.failCommand = failCommand;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ICommand getCommand() {
        return command;
    }

    public void setCommand(ICommand command) {
        this.command = command;
    }

    public ICommand getSuccessCommand() {
        return successCommand;
    }

    public void setSuccessCommand(ICommand successCommand) {
        this.successCommand = successCommand;
    }

    public ICommand getFailCommand() {
        return failCommand;
    }

    public void setFailCommand(ICommand failCommand) {
        this.failCommand = failCommand;
    }

    public abstract void run() throws ClientSideException;
}
