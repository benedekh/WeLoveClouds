package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.ecs.exceptions.ClientSideException;

public class Loglevel extends AbstractCommand {

    private static final Logger LOGGER = Logger.getLogger(Loglevel.class);
    
    public Loglevel(String[] arguments){
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        // TODO Auto-generated method stub

    }

}
