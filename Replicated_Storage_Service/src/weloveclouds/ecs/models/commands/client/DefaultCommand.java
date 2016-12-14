package weloveclouds.ecs.models.commands.client;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;

/**
 * Returned when an invalid command is parsed Created by Hunton on 2016-12-08
 * 
 * @author Benoit, hb
 */
public class DefaultCommand extends AbstractEcsClientCommand {

    public DefaultCommand(IKVEcsApi externalConfigurationServiceApi, String[] arguments) {
        super(externalConfigurationServiceApi, arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        throw new ClientSideException(CustomStringJoiner.join(" ", "Unable to find command."));
    }

    @Override
    public String toString() {
        return "Default Command";
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        // nothing to validate
        return this;
    }
}
