package weloveclouds.ecs.models.tasks;

import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.models.commands.AbstractCommand;

/**
 * Created by Benoit on 2016-11-20.
 */
public class SimpleRetryableTask extends AbstractRetryableTask {
    public SimpleRetryableTask(int maxNumberOfRetries, AbstractCommand command, AbstractCommand successCommand, AbstractCommand failCommand) {
        super(maxNumberOfRetries, command, successCommand, failCommand);
    }

    public SimpleRetryableTask(int maxNumberOfRetries, AbstractCommand command, AbstractCommand successCommand) {
        super(maxNumberOfRetries, command, successCommand, null);
    }

    public SimpleRetryableTask(int maxNumberOfRetries, AbstractCommand command) {
        super(maxNumberOfRetries, command, null, null);
    }

    @Override
    public void runCommand() throws ClientSideException {
        if (command != null) {
            command.execute();
        }
    }

    @Override
    public void runSuccessCommand() throws ClientSideException {
        if (successCommand != null) {
            successCommand.execute();
        }
    }

    @Override
    public void runFailCommand() throws ClientSideException {
        if (failCommand != null) {
            failCommand.execute();
        }
    }
}
