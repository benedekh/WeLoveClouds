package weloveclouds.client.models.commands;

import java.security.InvalidParameterException;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.commons.exceptions.ClientSideException;

/**
 * Represents a command that was not recognized as a valid
 * {@link weloveclouds.client.models.commands.ClientCommand}.
 *
 * @author Benoit, Hunton
 */
public class DefaultCommand extends AbstractCommand {

    public DefaultCommand(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        throw new ClientSideException(CustomStringJoiner.join(" ", "Unable to find command.",
                HelpMessageGenerator.generateHelpMessage()));
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        return this;
    }
}
