package weloveclouds.client.utils;

import weloveclouds.commons.cli.utils.AbstractUserInputParser;
import weloveclouds.client.models.commands.ClientCommand;

/**
 * Created by Benoit on 2016-11-20.
 */
public class ClientUserInputParser extends AbstractUserInputParser<ClientCommand> {
    @Override
    public ClientCommand getCommandFromEnum(String commandAsString) {
        return ClientCommand.createCommandFromString(commandAsString);
    }
}
