package weloveclouds.commons.jetty;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.EnumSet;

import weloveclouds.commons.configuration.annotations.JerseyResourcesConfigClass;
import weloveclouds.commons.configuration.annotations.WebServicePort;

/**
 * Created by Benoit on 2017-01-22.
 */
@Singleton
public class WebService extends Thread implements IWebService {
    private static final Logger LOGGER = Logger.getLogger(WebService.class);
    private int port;
    private String jerseyResourcesConfigClass;
    private Server jettyServer;

    @Inject
    public WebService(@WebServicePort int port, @JerseyResourcesConfigClass String jerseyResourcesConfigClass) {
        super();
        this.port = port;
        this.jerseyResourcesConfigClass = jerseyResourcesConfigClass;
    }

    private void initialize() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        jettyServer = new Server(port);

        ServletHolder holder = context.addServlet(ServletContainer.class, "/*");
        holder.setInitOrder(0);
        holder.setInitParameter("javax.ws.rs.Application", jerseyResourcesConfigClass);
        context.addServlet(holder, "/*");
        context.setContextPath("/");
        jettyServer.setHandler(context);
    }

    @Override
    public void run() {
        initialize();
        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            LOGGER.warn("Unable to start web service with cause: " + e.getMessage());
        } finally {
            jettyServer.destroy();
        }
    }
}