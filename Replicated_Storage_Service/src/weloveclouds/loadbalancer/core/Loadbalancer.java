package weloveclouds.loadbalancer.core;

import com.google.inject.Inject;

import weloveclouds.loadbalancer.core.handlers.requestInterceptors.ClientRequestInterceptor;
import weloveclouds.loadbalancer.core.handlers.requestInterceptors.EcsRequestInterceptor;
import weloveclouds.loadbalancer.core.handlers.requestInterceptors.KvServerRequestInterceptor;

/**
 * Created by Benoit on 2016-12-03.
 */
public class Loadbalancer {
    private ClientRequestInterceptor clientRequestHandler;
    private EcsRequestInterceptor ecsRequestHandler;
    private KvServerRequestInterceptor kvServerRequestHandler;

    @Inject
    public Loadbalancer(ClientRequestInterceptor clientRequestHandler, EcsRequestInterceptor
            ecsRequestHandler, KvServerRequestInterceptor kvServerRequestHandler) {
        this.clientRequestHandler = clientRequestHandler;
        this.ecsRequestHandler = ecsRequestHandler;
        this.kvServerRequestHandler = kvServerRequestHandler;
    }
}
