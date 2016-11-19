package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.exceptions.ClientSideException;

/**
 * 
 * @author hb
 *  
 */
public class AddNode extends AbstractEcsApiCommand{

    private static Logger logger;
    
    public AddNode(String[] arguments, IKVEcsApi ecsCommunicationApi) {
        super(arguments, ecsCommunicationApi);
        logger = Logger.getLogger(getClass());
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
