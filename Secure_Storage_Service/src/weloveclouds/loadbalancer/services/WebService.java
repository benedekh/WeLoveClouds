package weloveclouds.loadbalancer.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.loadbalancer.configuration.annotations.WebServicePort;

/**
 * Created by Benoit on 2017-01-22.
 */
@Singleton
public class WebService {
    private int port;
    private Server jettyServer;

    @Inject
    public WebService(@WebServicePort int port) {
        this.port = port;
        initialize();
    }

    private void initialize() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        jettyServer = new Server(port);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "weloveclouds.loadbalancer.rest.api");
        jerseyServlet.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
    }

    public void start() throws ServerSideException {

        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            throw new ServerSideException("Unable to start web service with cause: " + e
                    .getMessage());
        } finally {
            jettyServer.destroy();
        }
    }
}
