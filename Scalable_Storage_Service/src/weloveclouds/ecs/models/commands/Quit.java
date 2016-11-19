package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

public class Quit extends AbstractCommand {

    private static final Logger LOGGER = Logger.getLogger(Quit.class);
    
    public Quit(String[] arguments) {
        super(arguments);
    }

    
    
    @Override
    public ICommand validate() throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void execute() throws Exception {
        // TODO Auto-generated method stub

    }

}
