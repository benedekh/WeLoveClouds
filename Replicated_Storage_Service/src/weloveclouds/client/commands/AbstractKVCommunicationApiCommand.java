package weloveclouds.client.commands;

import weloveclouds.server.api.IKVCommunicationApi;

/**
 * Represents an {@link AbstractCommunicationApiCommand} that shall use the
 * {@link #communicationApi} in order to execute the command.
 *
 * @author Benedek
 */
public abstract class AbstractKVCommunicationApiCommand extends AbstractCommunicationApiCommand {

    protected IKVCommunicationApi communicationApi;

    /**
     * @param arguments the arguments of the command
     * @param communicationApi a reference to the communication module
     */
    public AbstractKVCommunicationApiCommand(String[] arguments,
            IKVCommunicationApi communicationApi) {
        super(arguments, communicationApi);
        this.communicationApi = communicationApi;
    }

}
