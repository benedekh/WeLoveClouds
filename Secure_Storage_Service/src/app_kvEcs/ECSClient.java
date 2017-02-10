package app_kvEcs;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.IOException;
import java.util.EnumSet;

import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.commons.configuration.InjectorHolder;
import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.commons.jetty.WebService;
import weloveclouds.commons.utils.LogSetup;
import weloveclouds.ecs.client.Client;
import weloveclouds.ecs.contexts.EcsExecutionContext;
import weloveclouds.ecs.configuration.modules.EcsClientModule;

public class ECSClient {
    private static Logger LOGGER = Logger.getLogger(ECSClient.class);
    private static UserOutputWriter userOutput = UserOutputWriter.getInstance();
    private static final String LOG_FILE = "logs/ecs.log";

    public static void main(String[] args) throws Exception {
        try {
            new LogSetup(LOG_FILE, Level.ALL);
            ExecutionContext.setExecutionEnvironmentSystemPropertiesFromArgs(args);
            EcsExecutionContext.setConfigurationFilePath(args[0]);

            Injector injector = Guice.createInjector(new EcsClientModule());
            InjectorHolder.getInstance().hold(injector);
            Client ecsClient = injector.getInstance(Client.class);
            WebService webService = injector.getInstance(WebService.class);
            ecsClient.start();
            webService.start();
        } catch (IOException ex) {
            userOutput.writeLine(ex.getMessage() + ex.getCause());
            LOGGER.error(ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex) {
            userOutput.writeLine("No ecs configuration file path provided.");
            LOGGER.fatal("No ecs configuration file path provided.");
        }
    }
}
