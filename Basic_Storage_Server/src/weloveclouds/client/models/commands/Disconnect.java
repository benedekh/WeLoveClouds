package weloveclouds.client.models.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Disconnect command which means a disconnection from the server.
 *
 * @author Benoit, Benedek, Hunton
 */
public class Disconnect extends AbstractCommunicationApiCommand {
    private static String SUCCESSFULLY_DISCONNECTED_MESSAGE =
            "Successfully disconnected from server.";

    private Logger logger;

    public Disconnect(String[] arguments, ICommunicationApi communicationApi) {
        super(arguments, communicationApi);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            logger.info("Executing disconnect command.");
            communicationApi.disconnect();
            logger.info("Disconnect was successful.");

            userOutputWriter.writeLine(SUCCESSFULLY_DISCONNECTED_MESSAGE);
            logger.debug(SUCCESSFULLY_DISCONNECTED_MESSAGE);
        } catch (IOException ex) {
            logger.error(ex);
            throw new ClientSideException(ex.getMessage(), ex);
        } finally {
            logger.info("Disconnect command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateDisconnectArguments(arguments);
        return this;
    }
}
