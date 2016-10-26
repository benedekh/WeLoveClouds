package ui;


import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import weloveclouds.client.core.Client;
import weloveclouds.client.models.commands.CommandFactory;
import weloveclouds.client.utils.StringJoiner;
import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.services.CommunicationService;

/**
 * @author Benoit
 */
public class Application {

    public static void main(String[] args) {
        initializeLogging();

        CommandFactory commandFactory = new CommandFactory(
                new CommunicationApiV1(new CommunicationService(new SocketFactory())));
        Client client = new Client(System.in, commandFactory);
        client.run();
    }

    private static void initializeLogging() {
        String logDir = "/log/client.log";
        PatternLayout pLayout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");

        try {
            Appender fa = new FileAppender(pLayout, logDir, true);
            Logger.getRootLogger().addAppender(fa);
        } catch (IOException ex) {
            System.err.println(
                    StringJoiner.join(" ", "Log file appender was not created.", ex.getMessage()));
        }

        Appender ca = new ConsoleAppender(pLayout);
        Logger.getRootLogger().addAppender(ca);
        Logger.getRootLogger().setLevel(Level.OFF);
    }
}
