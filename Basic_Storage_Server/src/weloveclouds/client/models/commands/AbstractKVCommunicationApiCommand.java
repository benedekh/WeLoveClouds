package weloveclouds.client.models.commands;

import weloveclouds.communication.api.v1.IKVCommunicationApi;

public abstract class AbstractKVCommunicationApiCommand extends AbstractCommunicationApiCommand {

    protected IKVCommunicationApi communicationApi;

    public AbstractKVCommunicationApiCommand(String[] arguments,
            IKVCommunicationApi communicationApi) {
        super(arguments, communicationApi);
        this.communicationApi = communicationApi;
    }

}
