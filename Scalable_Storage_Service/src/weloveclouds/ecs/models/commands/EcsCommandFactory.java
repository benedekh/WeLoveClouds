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
        ICommand recognizedCommand = null;

        switch (userInput.getCommand()) {
            case INIT_SERVICE:
                break;
            case START:
                break;
            case STOP:
                break;
            case SHUTDOWN:
                break;
            case ADD_NODE:
                break;
            case REMOVE_NODE:
                break;
            default:
                break;
        }
        return recognizedCommand;
    }
}
