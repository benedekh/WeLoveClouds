package app_kvClient;

import java.io.IOException;

import org.apache.log4j.Level;

import weloveclouds.client.core.Client;
import weloveclouds.client.models.commands.CommandFactory;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.server.api.IKVCommunicationApi;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.utils.LogSetup;

/**
 * 
 * Client application. See {@link Client} for more details.
 * 
 * @author Benoit, Benedek, Hunton
 */
public class KVClient {
    /**
     * The entry point of the application.
     * 
     * @param args is discarded so far
     */
    public static void main(String[] args) {
        String logFile = "logs/client.log";
        try {
            new LogSetup(logFile, Level.OFF);

            IKVCommunicationApi serverCommunication =
                    new KVCommunicationApiFactory().createKVCommunicationApiV1();
            CommandFactory commandFactory = new CommandFactory(serverCommunication);
            Client client = new Client(System.in, commandFactory);
            client.run();
        } catch (IOException ex) {
            System.err.println(CustomStringJoiner.join(" ", "Log file cannot be created on path ",
                    logFile, "due to an error:", ex.getMessage()));
        }
    }

}
