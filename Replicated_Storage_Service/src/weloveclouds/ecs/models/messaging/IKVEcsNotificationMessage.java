package weloveclouds.ecs.models.messaging;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-19.
 */
public interface IKVEcsNotificationMessage {
    enum StatusType {
        TOPOLOGY_UPDATE,
        HEALTH_UPDATE,
        RESPONSE_SUCCESS,
        RESPONSE_ERROR
    }

    RingTopology<StorageNode> getRingTopology();

    NodeHealthInfos getNodeHealthInfos();
}
