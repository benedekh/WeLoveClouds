package weloveclouds.client.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Logger;

import weloveclouds.client.commands.utils.ArgumentsValidator;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.communication.api.ICommunicationApi;

/**
 * Disconnect command which means a disconnection from the server.
 *
 * @author Benoit, Benedek, Hunton
 */
public class Disconnect extends AbstractCommunicationApiCommand {

    private static String SUCCESSFULLY_DISCONNECTED_MESSAGE =
            "Successfully disconnected from server.";
    private static final Logger LOGGER = Logger.getLogger(Disconnect.class);

    public Disconnect(String[] arguments, ICommunicationApi communicationApi) {
        super(arguments, communicationApi);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            LOGGER.info("Executing disconnect command.");
            communicationApi.disconnect();
            LOGGER.info("Disconnect was successful.");

            userOutputWriter.writeLine(SUCCESSFULLY_DISCONNECTED_MESSAGE);
            LOGGER.debug(SUCCESSFULLY_DISCONNECTED_MESSAGE);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new ClientSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("Disconnect command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateDisconnectArguments(arguments);
        return this;
    }
}
