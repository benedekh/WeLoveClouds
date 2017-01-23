package app_loadbalancer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.EnumSet;

import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.commons.utils.LogSetup;
import weloveclouds.loadbalancer.configuration.InjectorHolder;
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
            initWebServer();
        } catch (Exception ex) {
            userOutput.writeLine(ex.getMessage());
            LOGGER.error(ex.getMessage());
            System.exit(1);
        }
    }

    static void initWebServer() throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        Server server = new Server(8080);

        ServletHolder holder = context.addServlet(ServletContainer.class, "/*");
        holder.setInitOrder(0);
        holder.setInitParameter("javax.ws.rs.Application", "weloveclouds.loadbalancer" +
                ".configuration.JerseyConfig");
        context.addServlet(holder, "/*");
        context.setContextPath("/");
        server.setHandler(context);
        server.start();
        server.join();
    }
}
