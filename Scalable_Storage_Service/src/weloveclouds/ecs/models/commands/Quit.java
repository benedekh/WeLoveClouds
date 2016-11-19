package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;

public class Quit extends AbstractEcsApiCommand {

    /*  Quit extends AbstractEcsApiCommand as there may still be services running that need to be
        terminated when the admin decides to quit the ecs*/
    private static final Logger LOGGER = Logger.getLogger(Quit.class);
    
    public Quit(String[] arguments, IKVEcsApi ecsCommunicationApi) {
        super(arguments, ecsCommunicationApi);
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
