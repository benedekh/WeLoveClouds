package weloveclouds.client.commands;

import java.security.InvalidParameterException;

import weloveclouds.client.commands.utils.HelpMessageGenerator;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.utils.StringUtils;

/**
 * Represents a command that was not recognized as a valid {@link ClientCommand}.
 *
 * @author Benoit, Hunton
 */
public class DefaultCommand extends AbstractCommand {

    public DefaultCommand(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        throw new ClientSideException(StringUtils.join(" ", "Unable to find command.",
                HelpMessageGenerator.generateHelpMessage()));
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        return this;
    }
}
