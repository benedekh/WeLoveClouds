package weloveclouds.ecs.models.commands.client;

import java.io.IOException;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.HelpMessageGenerator;
import weloveclouds.ecs.utils.ArgumentsValidator;

public class Help extends AbstractEcsClientCommand{
    
    //TODO: It may be a good idea to put a logger here

    public Help(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        // TODO add help arguments to args validator
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }
    

}
