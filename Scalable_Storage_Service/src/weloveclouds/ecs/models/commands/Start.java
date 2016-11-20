package weloveclouds.ecs.models.commands;

import java.util.List;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.exceptions.ClientSideException;

/**
 * 
 * @author hb
 *
 */

public class Start<T> extends AbstractEcsApiCommand{
    
    private static final Logger LOGGER = Logger.getLogger(Start.class);
    
    public Start(List<T> arguments, IKVEcsApi ecsCommunicationApi) {
        super(arguments, ecsCommunicationApi);
    }

    @Override
    public void execute() throws ClientSideException {
        // TODO Auto-generated method stub
        
    }

}
