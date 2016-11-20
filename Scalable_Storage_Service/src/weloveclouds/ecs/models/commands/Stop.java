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

public class Stop<T> extends AbstractEcsApiCommand{

    private static final Logger LOGGER = Logger.getLogger(Stop.class);
    
    public Stop(List<T> arguments, IKVEcsApi escCommunicationApi) {
        super(arguments, escCommunicationApi);
    }

    @Override
    public void execute() throws ClientSideException {
        // TODO Auto-generated method stub
        
    }

}
