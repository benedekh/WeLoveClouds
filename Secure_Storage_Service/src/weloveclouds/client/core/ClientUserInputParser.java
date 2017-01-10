package weloveclouds.client.core;

import weloveclouds.client.commands.ClientCommand;
import weloveclouds.commons.cli.utils.AbstractUserInputParser;

/**
 * Parses the command-line inputs for KVClient.
 * 
 * @author Benoit
 */
public class ClientUserInputParser extends AbstractUserInputParser<ClientCommand> {
    @Override
    public ClientCommand getCommandFromEnum(String commandAsString) {
        return ClientCommand.createCommandFromString(commandAsString);
    }
}
