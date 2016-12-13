package weloveclouds.server.client.commands;

import weloveclouds.commons.exceptions.ServerSideException;

import static weloveclouds.commons.utils.StringUtils.join;
import static weloveclouds.server.client.commands.utils.HelpMessageGenerator.generateHelpMessage;


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
