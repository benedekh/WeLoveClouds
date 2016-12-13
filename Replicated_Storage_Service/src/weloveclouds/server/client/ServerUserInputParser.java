package weloveclouds.server.client;

import weloveclouds.commons.cli.utils.AbstractUserInputParser;
import weloveclouds.server.client.commands.ServerCommand;

/**
 * Created by Benoit on 2016-11-20.
 */
public class ServerUserInputParser extends AbstractUserInputParser<ServerCommand> {
    @Override
    public ServerCommand getCommandFromEnum(String commandAsString) {
        return ServerCommand.getValueFromDescription(commandAsString);
    }
}
