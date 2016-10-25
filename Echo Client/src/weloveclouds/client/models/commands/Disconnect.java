package weloveclouds.client.models.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.UserOutputWriter;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class Disconnect extends AbstractCommunicationApiCommand {
    private static String SUCCESSFULLY_DISCONNECTED_MESSAGE = "Successfully disconnected from " +
            "server";
    private UserOutputWriter userOutputWriter = UserOutputWriter.getInstance();

    public Disconnect(String[] arguments, ICommunicationApi communicationApi) {
        super(arguments, communicationApi);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.disconnect();
            userOutputWriter.writePrefix();
            userOutputWriter.writeLine(SUCCESSFULLY_DISCONNECTED_MESSAGE);
        } catch (IOException e) {
            throw new ClientSideException(e.getMessage());
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateDisconnectArguments(arguments);
        return this;
    }
}
