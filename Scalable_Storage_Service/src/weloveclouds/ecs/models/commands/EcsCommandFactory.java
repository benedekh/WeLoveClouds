package weloveclouds.ecs.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.cli.models.ParsedUserInput;

/**
 * command factory pattern, 
 * @author benoit, adapted for ECS codebase by hb
 *
 */
public class EcsCommandFactory {
    private static final Logger LOGGER = Logger.getLogger(EcsCommandFactory.class);
    private IKVEcsApi ecsCommunicationApi;

    //ecsApi will be an instance of the ecs service that the client can interact with.
    public EcsCommandFactory(IKVEcsApi ecsCommunicationApi){
        this.ecsCommunicationApi = ecsCommunicationApi;
    }
    
    //In the future this will invariably need to throw an exception for some things
    public ICommand createFromUserInput(ParsedUserInput input){
        ICommand recognizedCommand = null;
        EcsCommand userCommand = input.getEcsCommand();
        
        switch(userCommand){
            case INIT:
                recognizedCommand = new Init(input.getArguments(), ecsCommunicationApi);
            case ADD_NODE:
                recognizedCommand = new AddNode(input.getArguments(), ecsCommunicationApi);
                break;
            case START:
                recognizedCommand = new Start(input.getArguments(), ecsCommunicationApi);
                break;
            case STOP:
                recognizedCommand = new Stop(input.getArguments(), ecsCommunicationApi);
                break;
            case SHUTDOWN:
                recognizedCommand = new Shutdown(input.getArguments(), ecsCommunicationApi);
                break;
            case REMOVE_NODE:
                recognizedCommand = new RemoveNode(input.getArguments(), ecsCommunicationApi);
                break;
            case QUIT:
                recognizedCommand = new Quit(input.getArguments(), ecsCommunicationApi);
                break;
            case LOGLEVEL:
                recognizedCommand = new Loglevel(input.getArguments());
                break;
            default:
                LOGGER.info(join(" ", "Unrecognized command:", userCommand.toString()));
                recognizedCommand = new DefaultCommand(null);
                break;
        }
        return recognizedCommand;
    }
}