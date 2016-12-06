package weloveclouds.ecs.models.commands.client;

import com.google.inject.Inject;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.models.commands.CommandFactory;
import weloveclouds.commons.cli.models.ParsedUserInput;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.AbstractCommand;


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
                LOGGER.info(join(" ", "Unrecognized command:"));
                /*Default command doesn't actually need access to the API, I've just put it in here because
                if the super class doesn't get one it crashes*/
                recognizedCommand = new DefaultCommand(externalConfigurationServiceApi, userInput.getArguments());
                break;
        }
        return recognizedCommand;
    }
}
