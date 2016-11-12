package app_kvEcs;

import java.io.IOException;

import org.apache.log4j.Level;

import weloveclouds.client.core.Client;
import weloveclouds.client.models.commands.CommandFactory;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.v1.IKVCommunicationApi;
import weloveclouds.server.utils.LogSetup;

public class ECSClient {
    
    /**
     * The entry point of the application.
     * 
     * @param args is discarded so far
     */
    public static void main(String[] args){
        String logFile = "logs/client.log";
        try {
            new LogSetup(logFile, Level.OFF);
            //TODO: handle command line args in here.
        } catch (Exception e){
            //TODO: handle exception.
        }
   }


}
