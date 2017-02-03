package app_loadbalancer;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.commons.utils.LogSetup;
import weloveclouds.commons.configuration.InjectorHolder;
import weloveclouds.loadbalancer.configuration.LoadBalancerConfigurationFactory;
import weloveclouds.loadbalancer.configuration.LoadBalancerConfigurationHolder;
import weloveclouds.loadbalancer.configuration.modules.LoadBalancerModule;
import weloveclouds.loadbalancer.core.LoadBalancer;

/**
 * Created by Benoit on 2016-12-21.
 */
public class LoadbalancerClient {
    private static Logger LOGGER = Logger.getLogger(LoadbalancerClient.class);
    private static UserOutputWriter userOutput = UserOutputWriter.getInstance();
    private static final String LOG_FILE = "logs/loadbalancer.log";

    public static void main(String[] args) throws Exception {
        try {
            new LogSetup(LOG_FILE, Level.ALL);
            ExecutionContext.setExecutionEnvironmentSystemPropertiesFromArgs(args);
            LoadBalancerConfigurationHolder.register(LoadBalancerConfigurationFactory
                    .createLoadBalancerConfigurationFromArgs(args));

            Injector injector = Guice.createInjector(new LoadBalancerModule());
            InjectorHolder.getInstance().hold(injector);
            LoadBalancer loadBalancer = injector.getInstance(LoadBalancer.class);
            loadBalancer.start();
        } catch (Exception ex) {
            userOutput.writeLine(ex.getMessage());
            LOGGER.error(ex.getMessage());
            System.exit(1);
        }
    }
}
