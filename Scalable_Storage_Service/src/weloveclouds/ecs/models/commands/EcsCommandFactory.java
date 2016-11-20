package weloveclouds.ecs.models.commands;

import weloveclouds.cli.models.ParsedUserInput;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.client.EcsCommand;

/**
 * Created by Benoit on 2016-11-16.
 */
public class EcsCommandFactory {
    IKVEcsApi externalConfigurationServiceApi;

    public EcsCommandFactory(IKVEcsApi externalConfigurationServiceApi) {
        this.externalConfigurationServiceApi = externalConfigurationServiceApi;
    }

    public ICommand createCommandFromUserInput(ParsedUserInput<EcsCommand> userInput) {
        switch (userInput.getCommand()) {

        }
        return null;
    }
}
