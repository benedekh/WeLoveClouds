package weloveclouds.ecs.utils;

import weloveclouds.cli.utils.AbstractUserInputParser;
import weloveclouds.ecs.models.commands.client.EcsCommand;

/**
 * Created by Benoit on 2016-11-20.
 */
public class EcsClientUserInputParser extends AbstractUserInputParser<EcsCommand> {
    @Override
    public EcsCommand getCommandFromEnum(String commandAsString) {
        return EcsCommand.createCommandFromString(commandAsString);
    }
}
