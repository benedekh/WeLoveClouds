package weloveclouds.ecs.models.repository;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.core.ExternalConfigurationServiceConstants;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.loadbalancer.models.NodeHealthInfos;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

import static weloveclouds.ecs.models.repository.NodeStatus.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class StorageNode extends AbstractNode {
    private static final int NO_CONNECTION = 0;

    private NodeStatus metadataStatus;
    private NodeStatus status;
    private HashRange previousHashRange;
    private HashRange hashRange;
    private List<StorageNode> replicas;
    private List<HashRange> childHashRanges;

    private StorageNode(Builder storageNodeBuilder) {
        this.status = IDLE;
        this.metadataStatus = UNSYNCHRONIZED;
        this.name = storageNodeBuilder.name;
        this.serverConnectionInfo = storageNodeBuilder.serverConnectionInfo;
        this.hashKey = storageNodeBuilder.hashKey;
        this.hashRange = storageNodeBuilder.hashRange;
        this.ecsChannelConnectionInfo = storageNodeBuilder.ecsChannelConnectionInfo;
        this.replicas = storageNodeBuilder.replicas;
        this.childHashRanges = storageNodeBuilder.childHashRanges;
        this.previousHashRange = storageNodeBuilder.previousHashRange;

        if (storageNodeBuilder.healthInfos == null) {
            this.healthInfos = new NodeHealthInfos.Builder()
                    .nodeName(name)
                    .nodeStatus(HALTED)
                    .build();
        } else {
            this.healthInfos = storageNodeBuilder.healthInfos;
        }
    }

    public void updateHealthInfos(NodeHealthInfos healthInfos) {
        this.healthInfos = healthInfos;
    }

    public HashRange getHashRange() {
        return hashRange;
    }

    public void setHashRange(HashRange hashRange) {
        this.previousHashRange = hashRange;
        this.hashRange = hashRange;
        this.metadataStatus = SYNCHRONIZED;
    }

    public NodeStatus getMetadataStatus() {
        return metadataStatus;
    }

    public void setMetadataStatus(NodeStatus metadataStatus) {
        this.metadataStatus = metadataStatus;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public List<HashRange> getChildHashRanges() {
        return new ArrayList<>(childHashRanges);
    }

    public void addChildHashRange(HashRange childHashRange) {
        this.childHashRanges.add(childHashRange);
    }

    public void removeChildHashRange(HashRange childHashRange) {
        this.childHashRanges.remove(childHashRange);
    }

    public void clearChildHashRanges() {
        this.childHashRanges.clear();
    }

    public List<StorageNode> getReplicas() {
        return new ArrayList<>(replicas);
    }

    public void addReplicas(StorageNode node) {
        replicas.add(node);
    }

    public void removeReplicas(StorageNode node) {
        replicas.remove(node);
    }

    public void clearReplicas() {
        this.replicas.clear();
    }

    public boolean isReadResponsibleOf(Hash hash) {
        if (isWriteResponsibleOf(hash)) {
            return true;
        }

        for (HashRange hashRange : childHashRanges) {
            if (hashRange.contains(hash)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWriteResponsibleOf(Hash hash) {
        return hashRange.contains(hash);
    }

    public String toString() {
        return StringUtils.join(" ", "Node:" + getIpAddress() + "Status:" + status);
    }

    public static class Builder {
        private String name;
        private ServerConnectionInfo serverConnectionInfo;
        private ServerConnectionInfo ecsChannelConnectionInfo;
        private NodeHealthInfos healthInfos;
        private Hash hashKey;
        private HashRange previousHashRange;
        private HashRange hashRange;
        private List<StorageNode> replicas;
        private List<HashRange> childHashRanges;

        public Builder() {
            this.replicas = new ArrayList<>();
            this.childHashRanges = new ArrayList<>();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder serverConnectionInfo(ServerConnectionInfo serverConnectionInfo) {
            this.serverConnectionInfo = serverConnectionInfo;
            this.ecsChannelConnectionInfo = new ServerConnectionInfo.Builder()
                    .ipAddress(serverConnectionInfo.getIpAddress())
                    .port(ExternalConfigurationServiceConstants.ECS_REQUESTS_PORT).build();
            this.hashKey = HashingUtils.getHash(serverConnectionInfo.toString());
            return this;
        }

        public Builder healthInfos(NodeHealthInfos nodeHealthInfos) {
            this.healthInfos = nodeHealthInfos;
            return this;
        }

        public Builder previousHashRange(HashRange previousHashRange) {
            this.previousHashRange = previousHashRange;
            return this;
        }

        public Builder hashRange(HashRange hashRange) {
            this.hashRange = hashRange;
            return this;
        }

        public Builder replicas(List<StorageNode> replicas) {
            this.replicas = replicas;
            return this;
        }

        public Builder childHashRanges(List<HashRange> childHashRanges) {
            this.childHashRanges = childHashRanges;
            return this;
        }

        public StorageNode build() {
            return new StorageNode(this);
        }
    }
}
