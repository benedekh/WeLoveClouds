package weloveclouds.client.module;

import com.google.inject.AbstractModule;

import weloveclouds.client.utils.UserInputConverter;
import weloveclouds.client.utils.UserInputToApiRequestConverterV1;
import weloveclouds.communication.CommunicationModule;

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
