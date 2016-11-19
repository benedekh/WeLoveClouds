package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;

/**
 * 
 * @author hb
 *  
 */
public class AddNode extends AbstractEcsApiCommand{

    private static Logger logger;
    
    public AddNode(String[] arguments, IKVEcsApi ecsApi) {
        super(arguments, ecsApi);
        logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

}
