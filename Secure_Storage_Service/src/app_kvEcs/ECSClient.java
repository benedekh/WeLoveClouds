package app_kvEcs;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;

import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.commons.utils.LogSetup;
import weloveclouds.ecs.client.Client;
import weloveclouds.ecs.contexts.EcsExecutionContext;
import weloveclouds.ecs.configuration.modules.client.EcsClientModule;

public class ECSClient {
    private static Logger LOGGER = Logger.getLogger(ECSClient.class);
    private static UserOutputWriter userOutput = UserOutputWriter.getInstance();
    private static final String LOG_FILE = "logs/ecs.log";

    public static void main(String[] args) throws Exception {
        try {
            new LogSetup(LOG_FILE, Level.OFF);
            ExecutionContext.setExecutionEnvironmentSystemPropertiesFromArgs(args);
            EcsExecutionContext.setConfigurationFilePath(args[0]);

            Injector injector = Guice.createInjector(new EcsClientModule());
            Client ecsClient = injector.getInstance(Client.class);
            ecsClient.run();
        } catch (IOException ex) {
            userOutput.writeLine(ex.getMessage() + ex.getCause());
            LOGGER.error(ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex) {
            userOutput.writeLine("No ecs configuration file path provided.");
            LOGGER.fatal("No ecs configuration file path provided.");
        }
    }
}
