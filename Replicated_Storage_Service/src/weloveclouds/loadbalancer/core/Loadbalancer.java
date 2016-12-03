package weloveclouds.loadbalancer.core;

import com.google.inject.Inject;

import weloveclouds.loadbalancer.core.RequestHandler.ClientRequestHandler;
import weloveclouds.loadbalancer.core.RequestHandler.EcsRequestHandler;
import weloveclouds.loadbalancer.core.RequestHandler.KvServerRequestHandler;

/**
 * Created by Benoit on 2016-12-03.
 */
public class Loadbalancer {
    private ClientRequestHandler clientRequestHandler;
    private EcsRequestHandler ecsRequestHandler;
    private KvServerRequestHandler kvServerRequestHandler;

    @Inject
    public Loadbalancer(ClientRequestHandler clientRequestHandler, EcsRequestHandler
            ecsRequestHandler, KvServerRequestHandler kvServerRequestHandler) {
        this.clientRequestHandler = clientRequestHandler;
        this.ecsRequestHandler = ecsRequestHandler;
        this.kvServerRequestHandler = kvServerRequestHandler;
    }
}
