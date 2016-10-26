package weloveclouds.client.models.commands;

import weloveclouds.communication.api.ICommunicationApi;

/**
 * Created by Benoit on 2016-10-25.
 */
public abstract class AbstractCommunicationApiCommand extends AbstractCommand implements ICommand{
    protected ICommunicationApi communicationApi;

    public AbstractCommunicationApiCommand(String[] arguments, ICommunicationApi communicationApi) {
        super(arguments);
        this.communicationApi = communicationApi;
    }
}
