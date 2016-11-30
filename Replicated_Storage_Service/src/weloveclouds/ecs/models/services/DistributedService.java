package weloveclouds.ecs.models.services;

import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.ecs.models.topology.RingTopology;

import static weloveclouds.commons.status.ServiceStatus.UNINITIALIZED;

/**
 * Created by Benoit on 2016-11-30.
 */
public class DistributedService<StorageNode> {
    private RingTopology<StorageNode> topology;
    private ServiceStatus status;

    public DistributedService(){
        this.status = UNINITIALIZED;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public RingTopology<StorageNode> getTopology() {
        return topology;
    }

    public void updateTopologyWith(RingTopology ringTopology){

    }
}
