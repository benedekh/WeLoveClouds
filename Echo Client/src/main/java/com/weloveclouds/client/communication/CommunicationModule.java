package main.java.com.weloveclouds.client.communication;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.net.InetAddress;

import main.java.com.weloveclouds.client.communication.api.CommunicationApi;
import main.java.com.weloveclouds.client.communication.api.v1.CommunicationApiV1;
import main.java.com.weloveclouds.client.communication.models.RemoteServer;
import main.java.com.weloveclouds.client.communication.services.CommunicationService;

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
