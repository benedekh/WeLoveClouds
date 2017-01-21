package weloveclouds.ecs.models.messaging.notification;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-19.
 */
public class KVEcsNotificationMessage implements IKVEcsNotificationMessage {
    private RingTopology<StorageNode> ringTopology;
    private NodeHealthInfos nodeHealthInfos;
    private Status status;
    private List<String> unresponsiveNodesName;

    public KVEcsNotificationMessage(Builder builder) {
        this.status = builder.status;
        this.ringTopology = builder.ringTopology;
        this.nodeHealthInfos = builder.nodeHealthInfos;
        this.unresponsiveNodesName = builder.unresponsiveNodesName;
    }

    @Override
    public RingTopology<StorageNode> getRingTopology() {
        return ringTopology;
    }

    @Override
    public NodeHealthInfos getNodeHealthInfos() {
        return nodeHealthInfos;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public List<String> getUnresponsiveNodeNames() {
        return unresponsiveNodesName;
    }

    public static class Builder {
        private Status status;
        private RingTopology<StorageNode> ringTopology;
        private NodeHealthInfos nodeHealthInfos;
        private List<String> unresponsiveNodesName;

        public Builder() {
            this.unresponsiveNodesName = new ArrayList<>();
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder ringTopology(RingTopology<StorageNode> ringTopology) {
            this.ringTopology = ringTopology;
            return this;
        }

        public Builder nodeHealthInfos(NodeHealthInfos nodeHealthInfos) {
            this.nodeHealthInfos = nodeHealthInfos;
            return this;
        }

        public Builder unresponsiveNodesName(List<String> unresponsiveNodesName) {
            this.unresponsiveNodesName = unresponsiveNodesName;
            return this;
        }

        public Builder addUnresponsiveNodeName(String unresponsiveNodeName) {
            this.unresponsiveNodesName.add(unresponsiveNodeName);
            return this;
        }

        public Builder reset() {
            ringTopology = null;
            nodeHealthInfos = null;
            unresponsiveNodesName.clear();
            return this;
        }

        public KVEcsNotificationMessage build() {
            return new KVEcsNotificationMessage(this);
        }
    }
}
