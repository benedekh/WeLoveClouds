package weloveclouds.ecs.models.commands;

import weloveclouds.ecs.models.commands.AbstractCommand;
import weloveclouds.ecs.models.commands.ICommand;
//import weloveclouds.communication.api.ICommunicationApi;


/**
 * Represents an {@link AbstractCommand} that shall use the {@link #communicationApi} in order to
 * execute the command.
 *
 * @author Benoit
 * @author adapted from AbstractedCommunicationApiCommand by hb
 */
public abstract class AbstractEcsApiCommand extends AbstractCommand implements ICommand {
    //protected IEcsApi EcsApi <-- this remains to be implemented
    /**
     * @param arguments        the arguments of the command
     * @param communicationApi a reference to the communication module
     */
    public AbstractEcsApiCommand(String[] arguments /*there's going to be another parameter in here*/) {
        super(arguments);
        //this.communicationApi = communicationApi;
    }
}

