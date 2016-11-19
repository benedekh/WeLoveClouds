package weloveclouds.ecs.models.commands;

import java.security.InvalidParameterException;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * 
 * @author benoit, copied here by hb
 *
 */
public class DefaultCommand extends AbstractCommand{

    public DefaultCommand(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws Exception {
        throw new ClientSideException(CustomStringJoiner.join(" ", "Unable to find command.",
                HelpMessageGenerator.generateHelpMessage()));
        
    }

    @Override
    public ICommand validate() throws InvalidParameterException{
        return this;
    }

}

