package weloveclouds.ecs.models.commands;

import java.util.List;

import org.apache.log4j.Logger;

import weloveclouds.ecs.exceptions.ClientSideException;

public class Loglevel<T> extends AbstractCommand {

    private static final Logger LOGGER = Logger.getLogger(Loglevel.class);
    
    public Loglevel(List<T> arguments){
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        // TODO Auto-generated method stub

    }

}
