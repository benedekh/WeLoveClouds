package weloveclouds.ecs.models.messaging;

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

    public KVEcsNotificationMessage(Builder builder) {
        this.status = builder.status;
        this.ringTopology = builder.ringTopology;
        this.nodeHealthInfos = builder.nodeHealthInfos;
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

    public static class Builder {
        private Status status;
        private RingTopology<StorageNode> ringTopology;
        private NodeHealthInfos nodeHealthInfos;

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

        public KVEcsNotificationMessage build() {
            return new KVEcsNotificationMessage(this);
        }
    }
}
