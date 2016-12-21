package app_loadbalancer;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;

import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.commons.utils.LogSetup;
import weloveclouds.ecs.client.Client;
import weloveclouds.loadbalancer.configuration.modules.LoadBalancerModule;
import weloveclouds.loadbalancer.core.LoadBalancer;

/**
 * Created by Benoit on 2016-12-21.
 */
public class LoadbalancerClient {
    private static Logger LOGGER = Logger.getLogger(LoadbalancerClient.class);
    private static UserOutputWriter userOutput = UserOutputWriter.getInstance();
    private static final String LOG_FILE = "logs/ecs.log";

    public static void main(String[] args) throws Exception {
        try {
            new LogSetup(LOG_FILE, Level.OFF);
            ExecutionContext.setExecutionEnvironmentSystemPropertiesFromArgs(args);

            Injector injector = Guice.createInjector(new LoadBalancerModule());
            LoadBalancer loadBalancer = injector.getInstance(LoadBalancer.class);
            loadBalancer.start();
        } catch (IOException ex) {
            userOutput.writeLine(ex.getMessage() + ex.getCause());
            LOGGER.error(ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex) {
            userOutput.writeLine("No ecs configuration file path provided.");
            LOGGER.fatal("No ecs configuration file path provided.");
        }
    }
}
