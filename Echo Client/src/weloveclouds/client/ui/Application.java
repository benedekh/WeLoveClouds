package weloveclouds.client.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;

import weloveclouds.client.core.Client;
import weloveclouds.client.models.commands.CommandFactory;
import weloveclouds.client.module.ClientModule;
import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.services.CommunicationService;

/**
 * @author Benoit
 */
public class Application {

  public static void main(String[] args) {
    new Client(System.in, new CommandFactory(new CommunicationApiV1(new CommunicationService(new
            SocketFactory())))).run();
  }
}
