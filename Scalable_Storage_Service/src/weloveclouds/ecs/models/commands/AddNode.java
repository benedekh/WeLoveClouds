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
public class AddNode<T> extends AbstractEcsApiCommand{

    private static Logger logger;
    
    public AddNode(List<T> arguments, IKVEcsApi ecsCommunicationApi) {
        super(arguments, ecsCommunicationApi);
        logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        // TODO Auto-generated method stub
        
    }

}
