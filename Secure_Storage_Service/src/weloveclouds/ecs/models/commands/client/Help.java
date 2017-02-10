package weloveclouds.ecs.models.commands.client;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.HelpMessageGenerator;
import weloveclouds.ecs.utils.ArgumentsValidator;

/**
 * 
 * @author Hunton
 *
 */
public class Help extends AbstractEcsClientCommand {

    private static final Logger LOGGER = Logger.getLogger(Help.class);

    public Help(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateHelpArguments(arguments);
        return this;
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            userOutputWriter.writeLine(HelpMessageGenerator.generateHelpMessage());
            LOGGER.info("Help message written.");
        } catch (IOException e) {
            throw new ClientSideException(e.getMessage(), e);
        }

    }

    @Override
    public String toString() {
        return "Help Command";
    }


}
