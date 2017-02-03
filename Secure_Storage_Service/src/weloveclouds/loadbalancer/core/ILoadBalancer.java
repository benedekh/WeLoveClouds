package weloveclouds.loadbalancer.core;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.status.ServerStatus;

/**
 * Created by Benoit on 2016-12-06.
 */
public interface ILoadBalancer {
    void start() throws ServerSideException;
    ServerStatus getStatus();
}
