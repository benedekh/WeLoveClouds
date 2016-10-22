package weloveclouds.communication;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import weloveclouds.communication.api.CommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.models.RemoteServer;
import weloveclouds.communication.services.CommunicationService;

import java.net.InetAddress;

/**
 * Created by Benoit on 2016-10-21.
 */
public class CommunicationModule extends AbstractModule {

    @Provides
    public CommunicationService providesCommunicationService(){
        return new CommunicationService(new SocketFactory());
    }

    @Override
    protected void configure() {
        bind(CommunicationApi.class).to(CommunicationApiV1.class);
    }
}
