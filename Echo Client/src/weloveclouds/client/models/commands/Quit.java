package weloveclouds.client.models.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Quit command which terminates the application.
 * 
 * @author Benoit, Hunton
 */
public class Quit extends AbstractCommand {
    private static final String APPLICATION_EXITED_MESSAGE = "Application exit!";

    private Logger logger;

    public Quit(String[] arguments) {
        super(arguments);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            logger.info("Executing quit command.");
            userOutputWriter.writeLine(APPLICATION_EXITED_MESSAGE);
            logger.debug(APPLICATION_EXITED_MESSAGE);
            System.exit(0);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ClientSideException(e.getMessage(), e);
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateQuitArguments(arguments);
        return this;
    }
}
