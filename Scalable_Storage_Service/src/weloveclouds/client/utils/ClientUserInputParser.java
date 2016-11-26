package weloveclouds.client.utils;

import weloveclouds.commons.cli.utils.AbstractUserInputParser;
import weloveclouds.client.models.commands.Command;

/**
 * Created by Benoit on 2016-11-20.
 */
public class ClientUserInputParser extends AbstractUserInputParser<Command> {
    @Override
    public Command getCommandFromEnum(String commandAsString) {
        return Command.createCommandFromString(commandAsString);
    }
}
