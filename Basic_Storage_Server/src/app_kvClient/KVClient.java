package app_kvClient;

import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

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
        initializeLogging();
        ICommunicationApi serverCommunication =
                new CommunicationApiV1(new CommunicationService(new SocketFactory()));
        CommandFactory commandFactory =
                new CommandFactory(new KVCommunicationApiV1(serverCommunication, new
                        KVMessageSerializer(), new KVMessageDeserializer()));
        Client client = new Client(System.in, commandFactory);
        client.run();
    }

    /**
     * Initializes the loggers:<br>
     * (1) Sets the log layout pattern.<br>
     * (2) Sets the file appender's path (default: /log/client.log).<br>
     * (3) Registers the file appender and console appender for the root logger.<br>
     * (4) Disables logging by default.
     */
    private static void initializeLogging() {
        PatternLayout pLayout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");

        try {
            String logDir = "/log/client.log";
            Appender fa = new FileAppender(pLayout, logDir, true);
            Logger.getRootLogger().addAppender(fa);
        } catch (IOException ex) {
            System.err.println(CustomStringJoiner.join(" ", "Log file appender was not created.",
                    ex.getMessage()));
        }

        Appender ca = new ConsoleAppender(pLayout);
        Logger.getRootLogger().addAppender(ca);
        Logger.getRootLogger().setLevel(Level.OFF);
    }

}
