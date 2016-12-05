package weloveclouds.loadbalancer.core;

import com.google.inject.Inject;

import weloveclouds.loadbalancer.services.ClientRequestInterceptorService;

/**
 * Created by Benoit on 2016-12-03.
 */
public class Loadbalancer {
    private ClientRequestInterceptorService clientRequestHandler;


    @Inject
    public Loadbalancer(ClientRequestInterceptorService clientRequestHandler) {
        this.clientRequestHandler = clientRequestHandler;

    }
}
