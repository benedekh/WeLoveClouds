package weloveclouds.ecs.models.commands.client;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.AbstractCommand;
import weloveclouds.ecs.models.commands.ICommand;

/**
 * Returned when an invalid command is parsed
 * 
 * @author Benoit, hb
 */
public class DefaultCommand extends AbstractEcsClientCommand{

    public DefaultCommand(IKVEcsApi externalConfigurationServiceApi, String[] arguments) {
        //Passing null probably isn't a good idea.
        super(externalConfigurationServiceApi, arguments);
    }

    @Override
    public void execute() throws ClientSideException {
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
        return null;
    }

}
