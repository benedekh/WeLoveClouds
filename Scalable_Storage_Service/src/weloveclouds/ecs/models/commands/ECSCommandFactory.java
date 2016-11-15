package weloveclouds.ecs.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.ecs.service.api.IEcsApi;
import weloveclouds.cli.models.ParsedUserInput;

/**
 * command factory pattern, 
 * @author benoit, adapted for ECS codebase by hb
 *
 */
public class ECSCommandFactory {
    private IEcsApi ecsApi;
    private Logger logger;

    //ecsApi will be an instance of the ecs service that the client can interact with.
    public ECSCommandFactory(IEcsApi ecsApi){
        this.ecsApi = ecsApi;
        this.logger = Logger.getLogger(getClass());
    }
    
    //In the future this will invariably need to throw an exception for some things
    public ICommand createCommandFromInput(ParsedUserInput input){
        ICommand recognizedCommand = null;
        ECSCommand userCommand = input.getEcsCommand();
        
        switch(userCommand){
            case ADDNODE:
                recognizedCommand = new Addnode(input.getArguments());
                break;
            case START:
                recognizedCommand = new Start(input.getArguments());
                break;
            case STOP:
                recognizedCommand = new Stop(input.getArguments());
                break;
            case SHUTDOWN:
                recognizedCommand = new Shutdown(input.getArguments());
                break;
            case REMOVENODE:
                recognizedCommand = new Removenode(input.getArguments());
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
