package weloveclouds.ecs.models.commands.ssh;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.ecs.models.commands.AbstractCommand;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.ssh.AuthInfos;

/**
 * Created by Benoit on 2016-11-19.
 */
public abstract class AbstractRemoteCommand extends AbstractCommand<String> implements ICommand {
    private static final String ARGUMENTS_DELIMITER = " ";
    protected final String COMMAND;
    protected StorageNode targetedNode;

    public AbstractRemoteCommand(String command, List<String> arguments, StorageNode targettedNode) {
        this.COMMAND = command;
        this.arguments = arguments;
        this.targetedNode = targettedNode;
    }

    public String getTargettedHostIp() {
        return targetedNode.getIpAddress().replaceAll("/", "");
    }

    public StorageNode getTargetedNode() {
        return targetedNode;
    }

    public String toString() {
        ArrayList<String> commandAndArguments = new ArrayList<>();
        commandAndArguments.add(COMMAND);
        commandAndArguments.addAll(arguments);
        return CustomStringJoiner.join(ARGUMENTS_DELIMITER, commandAndArguments);
    }
}
