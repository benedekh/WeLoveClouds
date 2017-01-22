package weloveclouds.loadbalancer.core;

import weloveclouds.commons.exceptions.ServerSideException;

/**
 * Created by Benoit on 2016-12-06.
 */
public interface ILoadBalancer {
    void start() throws ServerSideException;
}
