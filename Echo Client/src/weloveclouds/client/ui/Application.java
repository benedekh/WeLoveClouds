package weloveclouds.client.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;

import weloveclouds.client.core.Client;
import weloveclouds.client.module.ClientModule;

import org.apache.log4j.*;

/**
 * The application class holds the main method.
 * @author Benoit
 */
public class Application {

  /**
   * The main method.
   * @param args - Our program does not take arguments at startup.
   * @see weloveclouds.client.core.Client#run()
   */
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new ClientModule());
    Client client = injector.getInstance(Client.class);
    client.run();
  }
}
