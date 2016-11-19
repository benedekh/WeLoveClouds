package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;

/**
 * 
 * @author hb
 *
 */

public class Start extends AbstractEcsApiCommand{
    
    private static final Logger LOGGER = Logger.getLogger(Start.class);
    
    public Start(String[] arguments, IKVEcsApi ecsApi) {
        super(arguments, ecsApi);
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
