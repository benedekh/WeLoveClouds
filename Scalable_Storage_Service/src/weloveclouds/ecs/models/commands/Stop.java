package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

/**
 * 
 * @author hb
 *
 */

public class Stop extends AbstractEcsApiCommand{

    private Logger logger;
    
    public Stop(String[] arguments) {
        super(arguments);
        this.logger = Logger.getLogger(getClass());
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
