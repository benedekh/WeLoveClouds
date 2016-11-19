package weloveclouds.ecs.models.commands;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.AbstractCommand;
import weloveclouds.ecs.models.commands.ICommand;



/**
 * Represents an {@link AbstractCommand} that shall use the {@link #communicationApi} in order to
 * execute the command.
 *
 * @author Benoit
 * @author adapted from AbstractedCommunicationApiCommand by hb
 */
public abstract class AbstractEcsApiCommand extends AbstractCommand implements ICommand {
    protected IKVEcsApi ecsApi; //<-- this remains to be implemented
    /**
     * @param arguments        the arguments of the command
     * @param communicationApi a reference to the communication module
     */
    public AbstractEcsApiCommand(String[] arguments, IKVEcsApi ecsApi) {
        super(arguments);
        this.ecsApi = ecsApi;
    }
}

