package weloveclouds.ecs.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.ecs.api.IKVEcsApi;

/**
 * 
 * @author hb
 *
 */

public class Shutdown extends AbstractEcsApiCommand{
   
    private static final Logger LOGGER = Logger.getLogger(Shutdown.class);
    
    public Shutdown(String[] arguments, IKVEcsApi ecsCommunicationApi) {
        super(arguments, ecsCommunicationApi);
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
