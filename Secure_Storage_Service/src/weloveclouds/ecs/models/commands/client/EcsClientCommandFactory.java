package weloveclouds.ecs.models.commands.client;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import weloveclouds.commons.cli.models.ParsedUserInput;
import weloveclouds.ecs.api.IKVEcsApi;

/**
 * Created by Benoit on 2016-11-16.
 */
public class EcsClientCommandFactory {
    private static final Logger LOGGER = Logger.getLogger(EcsClientCommandFactory.class);
    IKVEcsApi externalConfigurationServiceApi;

    @Inject
    public EcsClientCommandFactory(IKVEcsApi externalConfigurationServiceApi) {
        this.externalConfigurationServiceApi = externalConfigurationServiceApi;
    }

    public AbstractEcsClientCommand createCommandFromUserInput(ParsedUserInput<EcsCommand> userInput) {
        AbstractEcsClientCommand recognizedCommand;

        switch (userInput.getCommand()) {
            case START_LOAD_BALANCER:
                recognizedCommand = new StartLoadBalancer(externalConfigurationServiceApi, userInput
                        .getArguments());
                break;
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
            case HELP:
                recognizedCommand = new Help(externalConfigurationServiceApi, userInput.getArguments());
                break;
            case LOGLEVEL:
                recognizedCommand = new LogLevel(externalConfigurationServiceApi, userInput.getArguments());
                break;
            case QUIT:
                recognizedCommand = new Quit(externalConfigurationServiceApi, userInput.getArguments());
                break;
            case STATS:
                recognizedCommand = new Stats(externalConfigurationServiceApi, userInput
                        .getArguments());
                break;
            default:
                recognizedCommand = new DefaultCommand(externalConfigurationServiceApi, userInput.getArguments());
                break;
        }
        return recognizedCommand;
    }
}
