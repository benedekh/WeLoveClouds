package app_kvClient;

import java.io.IOException;

import org.apache.log4j.Level;

import weloveclouds.client.core.Client;
import weloveclouds.client.models.commands.CommandFactory;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.api.v1.KVCommunicationApiV1;
import weloveclouds.communication.services.CommunicationService;
import weloveclouds.kvstore.serialization.KVMessageDeserializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.server.utils.LogSetup;

/**
 * 
 * Echo client application. See {@link Client} for more details.
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

            ICommunicationApi serverCommunication =
                    new CommunicationApiV1(new CommunicationService(new SocketFactory()));
            CommandFactory commandFactory =
                    new CommandFactory(new KVCommunicationApiV1(serverCommunication,
                            new KVMessageSerializer(), new KVMessageDeserializer()));
            Client client = new Client(System.in, commandFactory);
            client.run();
        } catch (IOException ex) {
            System.err.println(CustomStringJoiner.join(" ", "Log file cannot be created on path ",
                    logFile, "due to an error:", ex.getMessage()));
        }
    }

}
