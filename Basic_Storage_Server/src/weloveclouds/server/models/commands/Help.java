package weloveclouds.server.models.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.server.utils.HelpMessageGenerator;
import weloveclouds.server.exceptions.ServerSideException;
import weloveclouds.server.utils.ArgumentsValidator;

/**
 * Prints the help message of the server-side CLI.
 *
 * @author Benedek
 */
public class Help extends AbstractServerCommand {

    private Logger logger;

    public Help(String[] arguments) {
        super(arguments);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            logger.info("Executing help command.");
            userOutputWriter.writeLine(HelpMessageGenerator.generateHelpMessage());
        } catch (IOException ex) {
            logger.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            logger.info("Help command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateHelpArguments(arguments);
        return this;
    }

}
