package weloveclouds.server.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import static weloveclouds.server.utils.HelpMessageGenerator.generateHelpMessage;

import weloveclouds.server.models.exceptions.ServerSideException;

public class DefaultCommand extends AbstractServerCommand {

    public DefaultCommand(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ServerSideException {
        throw new ServerSideException(join(" ", "Unable to find command.", generateHelpMessage()));
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        return this;
    }

}
