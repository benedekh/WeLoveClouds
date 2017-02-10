package weloveclouds.server.client.commands;

import static weloveclouds.server.client.commands.utils.HelpMessageGenerator.generateHelpMessage;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.utils.StringUtils;

/**
 * An unrecognized command.
 *
 * @author Benedek, Hunton
 */
public class DefaultCommand extends AbstractServerCommand {

    public DefaultCommand(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ServerSideException {
        throw new ServerSideException(
                StringUtils.join(" ", "Unable to find command.", generateHelpMessage()));
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        return this;
    }

}
