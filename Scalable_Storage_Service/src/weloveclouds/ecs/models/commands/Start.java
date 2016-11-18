package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

/**
 * 
 * @author hb
 *
 */

public class Start extends AbstractEcsApiCommand{
    
    private static final Logger LOGGER = Logger.getLogger(Start.class);
    
    public Start(String[] arguments) {
        super(arguments);
        // TODO Auto-generated constructor stub
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
