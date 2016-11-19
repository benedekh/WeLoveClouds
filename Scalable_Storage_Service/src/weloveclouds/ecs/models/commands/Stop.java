package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;

/**
 * 
 * @author hb
 *
 */

public class Stop extends AbstractEcsApiCommand{

    private static final Logger LOGGER = Logger.getLogger(Stop.class);
    
    public Stop(String[] arguments, IKVEcsApi escApi) {
        super(arguments, escApi);
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
