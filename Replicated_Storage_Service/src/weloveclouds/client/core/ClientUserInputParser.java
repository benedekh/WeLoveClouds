package weloveclouds.client.core;

import weloveclouds.client.commands.ClientCommand;
import weloveclouds.commons.cli.utils.AbstractUserInputParser;

/**
 * Created by Benoit on 2016-11-20.
 */
public class ClientUserInputParser extends AbstractUserInputParser<ClientCommand> {
    @Override
    public ClientCommand getCommandFromEnum(String commandAsString) {
        return ClientCommand.createCommandFromString(commandAsString);
    }
}
