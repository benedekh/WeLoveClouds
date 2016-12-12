package weloveclouds.ecs.models.commands.client;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;

/**
 * Returned when an invalid command is parsed
 * Created by Hunton on 2016-12-08
 * @author Benoit, hb
 */
public class DefaultCommand extends AbstractEcsClientCommand{
    //logger for debugging this, the program crashes instead of executing the generate help message
    private static Logger LOGGER = Logger.getLogger(DefaultCommand.class);

    public DefaultCommand(IKVEcsApi externalConfigurationServiceApi, String[] arguments) {
        super(externalConfigurationServiceApi, arguments);
        LOGGER.debug("Default command super constructor called successfully.");
    }

    @Override
    public void execute() throws ClientSideException {
        LOGGER.debug("Default command execute() executing");
        throw new ClientSideException(CustomStringJoiner.join(" ", "Unable to find command.",
                HelpMessageGenerator.generateHelpMessage()));
        
        
    }

    @Override
    public String toString() {
        //I don't know if this'll need to be converted to a string at any point.
        return null;
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        //nothing to validate
        return this;
    }

}
