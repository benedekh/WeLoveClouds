package weloveclouds.server.models.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.server.models.exceptions.ServerSideException;
import weloveclouds.server.utils.ArgumentsValidator;


/**
 * Quit command which terminates the application.
 *
 * @author Benoit, Hunton
 */
public class Quit extends AbstractServerCommand {

    private static final String APPLICATION_EXITED_MESSAGE = "Application exit!";

    private Logger logger;

    public Quit(String[] arguments) {
        super(arguments);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            logger.info("Executing quit command.");
            userOutputWriter.writeLine(APPLICATION_EXITED_MESSAGE);
            logger.debug(APPLICATION_EXITED_MESSAGE);
            System.exit(0);
        } catch (IOException e) {
            logger.error(e);
            throw new ServerSideException(e.getMessage(), e);
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateQuitArguments(arguments);
        return this;
    }


}
