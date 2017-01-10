package weloveclouds.server.client;

import weloveclouds.commons.cli.utils.AbstractUserInputParser;
import weloveclouds.server.client.commands.ServerCommand;

/**
 * Parses the command-line inputs for KVServer.
 * 
 * @author Benoit
 */
public class ServerUserInputParser extends AbstractUserInputParser<ServerCommand> {
    @Override
    public ServerCommand getCommandFromEnum(String commandAsString) {
        return ServerCommand.getValueFromDescription(commandAsString);
    }
}
