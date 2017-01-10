package weloveclouds.client.commands;

import weloveclouds.communication.api.ICommunicationApi;

/**
 * Represents an {@link AbstractCommand} that shall use the {@link #communicationApi} in order to
 * execute the command.
 *
 * @author Benoit
 */
public abstract class AbstractCommunicationApiCommand extends AbstractCommand implements ICommand {
    protected ICommunicationApi communicationApi;

    /**
     * @param arguments the arguments of the command
     * @param communicationApi a reference to the communication module
     */
    public AbstractCommunicationApiCommand(String[] arguments, ICommunicationApi communicationApi) {
        super(arguments);
        this.communicationApi = communicationApi;
    }
}
