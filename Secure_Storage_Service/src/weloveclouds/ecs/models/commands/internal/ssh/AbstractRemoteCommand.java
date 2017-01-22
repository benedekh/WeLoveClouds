package weloveclouds.ecs.models.commands.internal.ssh;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.ecs.models.commands.AbstractCommand;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.models.repository.AbstractNode;
import weloveclouds.ecs.models.repository.StorageNode;

/**
 * Created by Benoit on 2016-11-19.
 */
public abstract class AbstractRemoteCommand extends AbstractCommand<String>
        implements ICommand {
    private static final String ARGUMENTS_DELIMITER = " ";
    protected final String COMMAND;
    protected AbstractNode targetedNode;

    public AbstractRemoteCommand(String command, List<String> arguments, AbstractNode targettedNode) {
        this.COMMAND = command;
        this.arguments = arguments;
        this.targetedNode = targettedNode;
    }

    public String getTargetedHostIp() {
        return targetedNode.getIpAddress();
    }

    public AbstractNode getTargetedNode() {
        return targetedNode;
    }

    public String toString() {
        ArrayList<String> commandAndArguments = new ArrayList<>();
        commandAndArguments.add(COMMAND);
        commandAndArguments.addAll(arguments);
        return StringUtils.join(ARGUMENTS_DELIMITER, commandAndArguments);
    }
}
