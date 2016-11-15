package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

public class Loglevel extends AbstractEcsApiCommand {

    private Logger logger;
    
    public Loglevel(String[] arguments){
        super(arguments);
        this.logger = Logger.getLogger(getClass());
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
