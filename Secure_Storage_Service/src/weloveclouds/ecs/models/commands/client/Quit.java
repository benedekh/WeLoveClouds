package weloveclouds.ecs.models.commands.client;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.ArgumentsValidator;

/**
 * 
 * @author Benoit, Hunton
 *
 */
public class Quit extends AbstractEcsClientCommand {

    private static final String APPLICATION_EXIT_MESSAGE = "Exiting ECS application.";
    private static final Logger LOGGER = Logger.getLogger(Quit.class);

    public Quit(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateQuitArguments(arguments);
        return this;
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            LOGGER.info("Executing quit command");
            userOutputWriter.writeLine(APPLICATION_EXIT_MESSAGE);
            LOGGER.debug(APPLICATION_EXIT_MESSAGE);
            System.exit(0);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new ClientSideException(e.getMessage(), e);
        }

    }

    @Override
    public String toString() {
        return "Quit command";
    }

}
