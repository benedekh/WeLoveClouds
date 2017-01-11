package weloveclouds.ecs.models.messaging;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-19.
 */
public class KVEcsNotificationMessage implements IKVEcsNotificationMessage {
    @Override
    public RingTopology<StorageNode> getRingTopology() {
        return null;
    }

    @Override
    public NodeHealthInfos getNodeHealthInfos() {
        return null;
    }
}
