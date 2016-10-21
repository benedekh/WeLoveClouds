package main.java.com.weloveclouds.client.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;

import main.java.com.weloveclouds.client.core.Client;
import main.java.com.weloveclouds.client.module.BaseModule;

public class Application {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BaseModule());
        Client client = injector.getInstance(Client.class);
        client.run();
    }
}
