package weloveclouds.client.models.commands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class Send extends AbstractCommunicationApiCommand {
    private static final String MESSAGE_TERMINATOR = "\r";
    private static final String MESSAGE_PARTS_SEPARATOR = " ";
    private String message;

    public Send(String[] arguments, ICommunicationApi communicationApi) {
        super(arguments, communicationApi);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            message = (CustomStringJoiner.join(MESSAGE_PARTS_SEPARATOR, Arrays.asList(arguments))
                    + MESSAGE_TERMINATOR);
            communicationApi.send(message.getBytes());
            userOutputWriter
                    .writeLine(new String(communicationApi.receive(), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new ClientSideException(e.getMessage(), e);
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateSendArguments(arguments);
        return this;
    }
}
