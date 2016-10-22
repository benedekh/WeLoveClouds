package weloveclouds.communication;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.services.CommunicationService;

/**
 * @author Benoit
 */
public class CommunicationModule extends AbstractModule {

  @Provides
  public CommunicationService providesCommunicationService() {
    return new CommunicationService(new SocketFactory());
  }

  @Override
  protected void configure() {
    bind(ICommunicationApi.class).to(CommunicationApiV1.class);
  }
}
