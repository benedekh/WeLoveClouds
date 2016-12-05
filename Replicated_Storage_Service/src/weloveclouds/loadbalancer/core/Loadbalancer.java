package weloveclouds.loadbalancer.core;

import com.google.inject.Inject;

import weloveclouds.loadbalancer.services.requestInterceptors.ClientRequestInterceptor;

/**
 * Created by Benoit on 2016-12-03.
 */
public class Loadbalancer {
    private ClientRequestInterceptor clientRequestHandler;


    @Inject
    public Loadbalancer(ClientRequestInterceptor clientRequestHandler) {
        this.clientRequestHandler = clientRequestHandler;

    }
}
