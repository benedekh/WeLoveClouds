package weloveclouds.ecs.models.commands;

import java.security.InvalidParameterException;
import java.util.List;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.ecs.exceptions.ClientSideException;

/**
 * 
 * @author benoit, copied here by hb
 *
 */
public class DefaultCommand<T> extends AbstractCommand{

    public DefaultCommand(List<T> arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        throw new ClientSideException(CustomStringJoiner.join(" ", "Unable to find command.",
                HelpMessageGenerator.generateHelpMessage()));
        
    }

}

