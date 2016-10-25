package weloveclouds.client.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;

import weloveclouds.client.core.Client;
import weloveclouds.client.module.BaseModule;

import org.apache.log4j.*;

/**
 * @author Benoit
 */
public class Application {

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new BaseModule());
    Client client = injector.getInstance(Client.class);
    /**
     * @see weloveclouds.client.core.Client#run   
     */
    client.run();
  }
}
