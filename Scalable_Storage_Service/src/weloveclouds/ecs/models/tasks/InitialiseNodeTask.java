package weloveclouds.ecs.models.tasks;

import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.models.commands.UpdateServerRepository;
import weloveclouds.ecs.models.commands.ssh.LaunchJar;

/**
 * Created by Benoit on 2016-11-19.
 */
public class InitialiseNodeTask extends AbstractRetryableTask<LaunchJar, UpdateServerRepository, Object> {
    public InitialiseNodeTask(int maxNumberOfRetries, LaunchJar command, UpdateServerRepository
            succcessCommand) {
        super(maxNumberOfRetries, command, succcessCommand, null);
    }

    @Override
    public void runCommand() throws ClientSideException {
        command.execute();
    }

    @Override
    public void runSuccessCommand() throws ClientSideException {
        successCommand.addArguments(command.getTargetedNode());
        successCommand.execute();
    }

    @Override
    public void runFailCommand() throws ClientSideException {

    }
}
