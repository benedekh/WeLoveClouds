package weloveclouds.client.models.commands;

import static weloveclouds.client.utils.StringJoiner.join;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class Send extends AbstractCommunicationApiCommand {
    private static final String MESSAGE_TERMINATOR = "\r";
    private static final String MESSAGE_PARTS_SEPARATOR = " ";
    private String message;
    private Logger logger;

    public Send(String[] arguments, ICommunicationApi communicationApi) {
        super(arguments, communicationApi);
        this.message = join("", join(MESSAGE_PARTS_SEPARATOR, arguments), MESSAGE_TERMINATOR);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            logger.info("Executing send command.");
            communicationApi.send(message.getBytes(StandardCharsets.US_ASCII));
            logger.info("Message was sent.");

            logger.info("Receiving server response.");
            String response = new String(communicationApi.receive(), StandardCharsets.US_ASCII);
            userOutputWriter.writeLine(response);
            logger.debug(response);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ClientSideException(e.getMessage(), e);
        } finally {
            logger.info("Send command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        // because of the terminator character, the message shall be validated instead of the raw
        // arguments array
        ArgumentsValidator.validateSendArguments(new String[] {message});
        return this;
    }
}
