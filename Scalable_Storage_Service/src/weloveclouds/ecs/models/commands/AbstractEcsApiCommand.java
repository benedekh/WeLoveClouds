package weloveclouds.ecs.models.commands;

import java.util.List;

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
public abstract class AbstractEcsApiCommand<T> extends AbstractCommand implements ICommand {
    protected IKVEcsApi ecsCommunicationApi; //<-- this remains to be implemented
    /**
     * @param arguments        the arguments of the command
     * @param communicationApi a reference to the communication module
     */
    public AbstractEcsApiCommand(List<T> arguments, IKVEcsApi ecsCommunicationApi) {
        super(arguments);
        this.ecsCommunicationApi = ecsCommunicationApi;
    }
}

