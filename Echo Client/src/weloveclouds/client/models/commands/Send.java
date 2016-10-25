package weloveclouds.client.models.commands;

import java.util.Arrays;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class Send extends AbstractCommunicationApiCommand {
    private static final String MESSAGE_PARTS_SEPARATOR = " ";
    private byte[] message;

    public Send(String[] arguments, ICommunicationApi communicationApi) {
        super(arguments, communicationApi);
        this.message = String.join(MESSAGE_PARTS_SEPARATOR, Arrays.asList(arguments)).getBytes();
    }

    @Override
    public void execute() throws ClientSideException {
        communicationApi.send(message);
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateSendArguments(arguments);
        return this;
    }
}
