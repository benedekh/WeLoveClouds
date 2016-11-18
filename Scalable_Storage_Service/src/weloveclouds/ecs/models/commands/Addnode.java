package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

/**
 * 
 * @author hb
 *  This class contains only stubs
 */
public class Addnode extends AbstractEcsApiCommand{

    private static final Logger LOGGER = Logger.getLogger(Addnode.class);
    
    public Addnode(String[] arguments) {
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
