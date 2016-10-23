package weloveclouds.client.module;

import com.google.inject.AbstractModule;

import weloveclouds.communication.CommunicationModule;

/**
 * @author Benoit, Benedek
 */
public class ClientModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new CommunicationModule());
  }
}
