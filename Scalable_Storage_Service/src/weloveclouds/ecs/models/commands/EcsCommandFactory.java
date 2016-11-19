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
    private static Logger logger;
    private IKVEcsApi ecsCommsApi;

    //ecsApi will be an instance of the ecs service that the client can interact with.
    public EcsCommandFactory(IKVEcsApi ecsApi){
        this.ecsCommsApi = ecsApi;
        this.logger = Logger.getLogger(getClass());
    }
    
    //In the future this will invariably need to throw an exception for some things
    public ICommand createCommandFromInput(ParsedUserInput input){
        ICommand recognizedCommand = null;
        EcsCommand userCommand = input.getEcsCommand();
        
        switch(userCommand){
            case INIT:
                recognizedCommand = new Init(input.getArguments(), ecsCommsApi);
            case ADD_NODE:
                recognizedCommand = new AddNode(input.getArguments(), ecsCommsApi);
                break;
            case START:
                recognizedCommand = new Start(input.getArguments(), ecsCommsApi);
                break;
            case STOP:
                recognizedCommand = new Stop(input.getArguments(), ecsCommsApi);
                break;
            case SHUTDOWN:
                recognizedCommand = new Shutdown(input.getArguments(), ecsCommsApi);
                break;
            case REMOVE_NODE:
                recognizedCommand = new RemoveNode(input.getArguments(), ecsCommsApi);
                break;
            case QUIT:
                recognizedCommand = new Quit(input.getArguments());
                break;
            case LOGLEVEL:
                recognizedCommand = new Loglevel(input.getArguments());
                break;
            default:
                logger.info(join(" ", "Unrecognized command:", userCommand.toString()));
                recognizedCommand = new DefaultCommand(null);
                break;
        }
        return recognizedCommand;
    }
}
