package weloveclouds.client.models.commands;

import java.security.InvalidParameterException;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class Disconnect extends AbstractCommunicationApiCommand {

    public Disconnect(String[] arguments, ICommunicationApi communicationApi) {
        super(arguments, communicationApi);
    }

    @Override
    public void execute() throws ClientSideException {
        communicationApi.disconnect();
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateDisconnectArguments(arguments);
        return this;
    }
}
