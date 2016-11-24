package weloveclouds.client.models.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Help command which means a printing the help of the application.
 *
 * @author Benoit, Benedek
 */
public class Help extends AbstractCommand {

    private static final Logger LOGGER = Logger.getLogger(Help.class);

    public Help(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            LOGGER.info("Executing help command.");
            userOutputWriter.writeLine(HelpMessageGenerator.generateHelpMessage());
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new ClientSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("Help command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateHelpArguments(arguments);
        return this;
    }
}
