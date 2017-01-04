package weloveclouds.loadbalancer.models;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.ecs.models.messaging.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;

/**
 * Created by Benoit on 2016-12-21.
 */
public class EcsNotification implements IKVEcsNotificationMessage {
    private List<String> unrespondingNodesNames;

    protected EcsNotification(Builder builder) {
        this.unrespondingNodesNames = builder.unresponsiveNodesNames;
    }

    @Override
    public RingTopology<StorageNode> getRingTopology() {
        return null;
    }

    @Override
    public NodeHealthInfos getNodeHealthInfos() {
        return null;
    }

    @Override
    public Status getStatus() {
        return null;
    }

    public static class Builder {
        private List<String> unresponsiveNodesNames;

        public Builder() {
            this.unresponsiveNodesNames = new ArrayList<>();
        }

        public Builder addUnrespondingNodeName(String nodeName) {
            this.unresponsiveNodesNames.add(nodeName);
            return this;
        }

        public EcsNotification build() {
            return new EcsNotification(this);
        }
    }
}
