package main.java.com.weloveclouds.client.module;

import com.google.inject.AbstractModule;

import main.java.com.weloveclouds.client.communication.CommunicationModule;
import main.java.com.weloveclouds.client.utils.UserInputConverter;
import main.java.com.weloveclouds.client.utils.UserInputToApiRequestConverterV1;

/**
 * Created by Benoit on 2016-10-21.
 */
public class BaseModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(UserInputConverter.class).to(UserInputToApiRequestConverterV1.class);

        install(new CommunicationModule());
    }
}
