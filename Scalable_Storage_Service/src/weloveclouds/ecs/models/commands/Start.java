package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.exceptions.ClientSideException;

/**
 * 
 * @author hb
 *
 */

public class Start extends AbstractEcsApiCommand{
    
    private static final Logger LOGGER = Logger.getLogger(Start.class);
    
    public Start(String[] arguments, IKVEcsApi ecsCommunicationApi) {
        super(arguments, ecsCommunicationApi);
    }

    @Override
    public void execute() throws ClientSideException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

}