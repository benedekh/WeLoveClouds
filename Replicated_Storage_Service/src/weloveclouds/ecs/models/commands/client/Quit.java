package weloveclouds.ecs.models.commands.client;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.utils.ArgumentsValidator;

/**
 * 
 * @author Benoit, hb
 *
 */
public class Quit extends AbstractEcsClientCommand{

    private static final String APPLICATION_EXIT_MESSAGE = "Exiting ECS application.";
    private static final Logger LOGGER = Logger.getLogger(Quit.class);
    
    public Quit(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateQuitArguments(arguments);
        return this;
    }

    @Override
    public void execute() throws ClientSideException {
        try{
            LOGGER.info("Executing quit command");
            userOutputWriter.writeLine(APPLICATION_EXIT_MESSAGE);
            LOGGER.debug(APPLICATION_EXIT_MESSAGE);
            try{
                externalCommunicationServiceApi.shutDown();
            } catch(Exception e){
                userOutputWriter.writeLine("Shutdown of storage service failed!");
                LOGGER.debug("Shutdown failed");
                LOGGER.error(e.getMessage());
            }
            System.exit(0);//good exit, use 0
        }catch (IOException ioe){
            LOGGER.error(ioe);
            throw new ClientSideException(ioe.getMessage(), ioe);
        }
        
    }

    @Override
    public String toString() {
        //no point
        return null;
    }

}
