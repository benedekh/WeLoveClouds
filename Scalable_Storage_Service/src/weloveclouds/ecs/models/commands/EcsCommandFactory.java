package weloveclouds.ecs.models.commands;

import weloveclouds.cli.models.ParsedUserInput;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.client.EcsCommand;
import weloveclouds.ecs.models.commands.client.RemoveNode;
import weloveclouds.ecs.models.commands.client.ShutDown;
import weloveclouds.ecs.models.commands.client.Start;
import weloveclouds.ecs.models.commands.client.Stop;


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
                recognizedCommand = new Start(externalConfigurationServiceApi);
                break;
            case STOP:
                recognizedCommand = new Stop(externalConfigurationServiceApi);
                break;
            case SHUTDOWN:
                recognizedCommand = new ShutDown(externalConfigurationServiceApi);
                break;
            case ADD_NODE:
                break;
            case REMOVE_NODE:
                recognizedCommand = new RemoveNode(externalConfigurationServiceApi);
                break;
            default:
                break;
        }
        return recognizedCommand;
    }
}
