package weloveclouds.server.commands.client;

import weloveclouds.commons.exceptions.ServerSideException;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import static weloveclouds.server.utils.HelpMessageGenerator.generateHelpMessage;


/**
 * An unrecognized command.
 *
 * @author Benedek
 */
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
