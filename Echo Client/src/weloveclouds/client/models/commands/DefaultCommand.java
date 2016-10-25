package weloveclouds.client.models.commands;

import java.security.InvalidParameterException;

import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.communication.api.ICommunicationApi;
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
        throw new ClientSideException("Unable to find command. " + HelpMessageGenerator.generateHelpMessage());
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        return this;
    }
}
