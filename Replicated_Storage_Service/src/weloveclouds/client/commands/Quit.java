package weloveclouds.client.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Logger;

import weloveclouds.client.commands.utils.ArgumentsValidator;
import weloveclouds.commons.exceptions.ClientSideException;

/**
 * Quit command which terminates the application.
 *
 * @author Benoit, Hunton
 */
public class Quit extends AbstractCommand {

    private static final String APPLICATION_EXITED_MESSAGE = "Application exit!";
    private static final Logger LOGGER = Logger.getLogger(Quit.class);

    public Quit(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            LOGGER.info("Executing quit command.");
            userOutputWriter.writeLine(APPLICATION_EXITED_MESSAGE);
            LOGGER.debug(APPLICATION_EXITED_MESSAGE);
            System.exit(0);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new ClientSideException(e.getMessage(), e);
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateQuitArguments(arguments);
        return this;
    }
}
