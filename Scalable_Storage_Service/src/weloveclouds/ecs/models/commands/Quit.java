package weloveclouds.ecs.models.commands;

import java.util.List;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.exceptions.ClientSideException;

public class Quit<T> extends AbstractEcsApiCommand {

    /*  Quit extends AbstractEcsApiCommand as there may still be services running that need to be
        terminated when the admin decides to quit the ecs*/
    private static final Logger LOGGER = Logger.getLogger(Quit.class);
    
    public Quit(List<T> arguments, IKVEcsApi ecsCommunicationApi) {
        super(arguments, ecsCommunicationApi);
    }


    @Override
    public void execute() throws ClientSideException {
        // TODO Auto-generated method stub

    }

}
