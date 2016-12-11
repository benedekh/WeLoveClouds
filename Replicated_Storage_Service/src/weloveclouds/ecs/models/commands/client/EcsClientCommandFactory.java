package weloveclouds.ecs.models.commands.client;

import com.google.inject.Inject;

import weloveclouds.commons.cli.models.ParsedUserInput;
import weloveclouds.ecs.api.IKVEcsApi;


/**
 * Created by Benoit on 2016-11-16.
 */
public class EcsClientCommandFactory {
    IKVEcsApi externalConfigurationServiceApi;

    @Inject
    public EcsClientCommandFactory(IKVEcsApi externalConfigurationServiceApi) {
        this.externalConfigurationServiceApi = externalConfigurationServiceApi;
    }

    public AbstractEcsClientCommand createCommandFromUserInput(ParsedUserInput<EcsCommand> userInput) {
        AbstractEcsClientCommand recognizedCommand = null;

        switch (userInput.getCommand()) {
            case INIT_SERVICE:
                recognizedCommand = new InitService(externalConfigurationServiceApi, userInput.getArguments());
                break;
            case START:
                recognizedCommand = new Start(externalConfigurationServiceApi, userInput.getArguments());
                break;
            case STOP:
                recognizedCommand = new Stop(externalConfigurationServiceApi, userInput.getArguments());
                break;
            case SHUTDOWN:
                recognizedCommand = new Shutdown(externalConfigurationServiceApi, userInput.getArguments());
                break;
            case ADD_NODE:
                recognizedCommand = new AddNode(externalConfigurationServiceApi, userInput.getArguments());
                break;
            case REMOVE_NODE:
                recognizedCommand = new RemoveNode(externalConfigurationServiceApi, userInput.getArguments());
                break;
            default:
                break;
        }
        return recognizedCommand;
    }
}
