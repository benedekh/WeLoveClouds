package weloveclouds.client.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;

import weloveclouds.client.core.Client;
import weloveclouds.client.module.ClientModule;

import org.apache.log4j.*;

/**
 * @author Benoit
 */
public class Application {

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new ClientModule());
    Client client = injector.getInstance(Client.class);
    /**
     * @see weloveclouds.client.core.Client#run   
     */
    client.run();
  }
}
