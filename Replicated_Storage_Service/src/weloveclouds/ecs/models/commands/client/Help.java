package weloveclouds.ecs.models.commands.client;

import java.io.IOException;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.HelpMessageGenerator;
import weloveclouds.ecs.utils.ArgumentsValidator;

/**
 * 
 * @author hb
 *
 */
public class Help extends AbstractEcsClientCommand{
    
    //TODO: It may be a good idea to put a logger here

    public Help(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        //arguments referring to the attribute of the super class
        ArgumentsValidator.validateHelpArguments(arguments);
        return this;
    }

    @Override
    public void execute() throws ClientSideException {
        try{
            userOutputWriter.writeLine(HelpMessageGenerator.generateHelpMessage());
        }catch(IOException ioe){
            throw new ClientSideException(ioe.getMessage(), ioe);
        }
        
    }

    @Override
    public String toString() {
        //No point
        return null;
    }
    

}
