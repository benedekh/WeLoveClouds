package weloveclouds.server.client.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.server.client.commands.utils.ArgumentsValidator;
import weloveclouds.server.client.commands.utils.HelpMessageGenerator;

/**
 * Prints the help message of the server-side CLI.
 *
 * @author Benedek, Hunton
 */
public class Help extends AbstractServerCommand {

    private static final Logger LOGGER = Logger.getLogger(Help.class);

    public Help(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            LOGGER.info("Executing help command.");
            userOutputWriter.writeLine(HelpMessageGenerator.generateHelpMessage());
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("Help command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateHelpArguments(arguments);
        return this;
    }

}
