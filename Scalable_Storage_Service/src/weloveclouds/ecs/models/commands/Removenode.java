package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

/**
 * 
 * @author hb
 *
 */

public class Removenode extends AbstractEcsApiCommand{
    
    private static final Logger LOGGER = Logger.getLogger(Removenode.class);
    
    public Removenode(String[] arguments) {
        super(arguments);
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
