package weloveclouds.client.models.commands;

import java.security.InvalidParameterException;

import org.mockito.internal.util.StringJoiner;

import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class DefaultCommand extends AbstractCommand {

    public DefaultCommand(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        throw new ClientSideException(StringJoiner.join(" ", "Unable to find command.",
                HelpMessageGenerator.generateHelpMessage()));
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        return this;
    }
}
