package app_kvEcs;

import java.io.IOException;

import org.apache.log4j.Level;
import weloveclouds.server.utils.LogSetup;
import weloveclouds.ecs.configuration.providers.AuthConfigurationProvider;

public class ECSClient {    
    /**
     * The entry point of the application.
     * 
     * @param args is discarded so far
     */
    public static void main(String[] args){
        AuthConfigurationProvider auth = AuthConfigurationProvider.getInstance();
        System.out.println(auth.getPassword());
        String logFile = "logs/client.log";
        try {
            new LogSetup(logFile, Level.OFF);
            //TODO: handle command line args in here.
        } catch (Exception e){
            //TODO: handle exception.
        }
   }
}


