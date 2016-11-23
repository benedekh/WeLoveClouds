package weloveclouds.ecs.models.commands.client;

import weloveclouds.cli.models.ParsedUserInput;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.client.AbstractEcsClientCommand;
import weloveclouds.ecs.models.commands.client.AddNode;
import weloveclouds.ecs.models.commands.client.EcsCommand;
import weloveclouds.ecs.models.commands.client.InitService;
import weloveclouds.ecs.models.commands.client.RemoveNode;
import weloveclouds.ecs.models.commands.client.Shutdown;
import weloveclouds.ecs.models.commands.client.Start;
import weloveclouds.ecs.models.commands.client.Stop;


/**
 * Created by Benoit on 2016-11-16.
 */
public class EcsClientCommandFactory {
    IKVEcsApi externalConfigurationServiceApi;

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
