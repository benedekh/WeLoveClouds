package weloveclouds.client.models.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class Disconnect extends AbstractCommunicationApiCommand {
    private static String SUCCESSFULLY_DISCONNECTED_MESSAGE = "Successfully disconnected from " +
            "server";

    public Disconnect(String[] arguments, ICommunicationApi communicationApi) {
        super(arguments, communicationApi);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.disconnect();
            userOutputWriter.writeLine(SUCCESSFULLY_DISCONNECTED_MESSAGE);
        } catch (IOException ex) {
            throw new ClientSideException(ex.getMessage(), ex);
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateDisconnectArguments(arguments);
        return this;
    }
}
