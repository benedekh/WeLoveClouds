package weloveclouds.server.client.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.server.client.commands.utils.ArgumentsValidator;


/**
 * Quit command which terminates the application.
 *
 * @author Benoit, Hunton
 */
public class Quit extends AbstractServerCommand {

    private static final String APPLICATION_EXITED_MESSAGE = "Application exit!";
    private static final Logger LOGGER = Logger.getLogger(Quit.class);

    public Quit(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            LOGGER.info("Executing quit command.");
            userOutputWriter.writeLine(APPLICATION_EXITED_MESSAGE);
            LOGGER.debug(APPLICATION_EXITED_MESSAGE);
            System.exit(0);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new ServerSideException(e.getMessage(), e);
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateQuitArguments(arguments);
        return this;
    }


}
