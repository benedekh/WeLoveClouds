package weloveclouds.ecs.models.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.ecs.models.repository.NodeStatus.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class StorageNode extends AbstractNode {
    private NodeStatus metadataStatus;
    private HashRange previousHashRange;
    private HashRange hashRange;
    private List<StorageNode> replicas;
    private List<HashRange> readRanges;
    private ServerConnectionInfo kvChannelConnectionInfo;
    private Hash hashKey;

    private StorageNode(Builder builder) {
        this.status = IDLE;
        this.metadataStatus = UNSYNCHRONIZED;
        this.name = builder.name;
        this.serverConnectionInfo = builder.serverConnectionInfo;
        this.ecsChannelConnectionInfo = builder.ecsChannelConnectionInfo;
        this.kvChannelConnectionInfo = builder.kvChannelConnectionInfo;
        this.hashKey = builder.hashKey;
        this.hashRange = builder.hashRange;
        this.replicas = builder.replicas;
        this.readRanges = builder.readRanges;
        this.previousHashRange = builder.previousHashRange;

        if (builder.healthInfos == null) {
            this.healthInfos = new NodeHealthInfos.Builder()
                    .nodeName(name)
                    .nodeStatus(HALTED)
                    .build();
        } else {
            this.healthInfos = builder.healthInfos;
        }
    }

    public ServerConnectionInfo getKvChannelConnectionInfo() {
        return kvChannelConnectionInfo;
    }

    public Hash getHashKey() {
        return hashKey;
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

    public List<HashRange> getReadRanges() {
        return new ArrayList<>(readRanges);
    }

    public void addReadRange(HashRange readRange) {
        readRanges.add(readRange);
    }

    public void clearReadRanges() {
        readRanges.clear();
    }

    public void clearHashRange() {
        setHashRange(null);
    }

    public List<StorageNode> getReplicas() {
        return new ArrayList<>(replicas);
    }

    public void addReplica(StorageNode node) {
        replicas.add(node);
    }

    public void clearReplicas() {
        this.replicas.clear();
    }

    public boolean isWriteResponsibleOf(Hash hash) {
        return hashRange.contains(hash);
    }

    public String toString() {
        String hashRangeAsString = "";
        if (hashRange != null) {
            hashRangeAsString = hashRange.toString();
        }
        return StringUtils.join(" ", "Node:", name, "Host:", getIpAddress(), "Status:", status,
                "Write range:", hashRangeAsString);
    }

    public static class Builder {
        private String name;
        private ServerConnectionInfo serverConnectionInfo;
        private ServerConnectionInfo ecsChannelConnectionInfo;
        private ServerConnectionInfo kvChannelConnectionInfo;
        private NodeHealthInfos healthInfos;
        private Hash hashKey;
        private HashRange previousHashRange;
        private HashRange hashRange;
        private List<StorageNode> replicas;
        private List<HashRange> readRanges;

        public Builder() {
            this.replicas = new ArrayList<>();
            this.readRanges = new ArrayList<>();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder serverConnectionInfo(ServerConnectionInfo serverConnectionInfo) {
            this.serverConnectionInfo = serverConnectionInfo;
            this.hashKey = HashingUtils.getHash(serverConnectionInfo.toString());
            return this;
        }

        public Builder ecsChannelConnectionInfo(ServerConnectionInfo ecsChannelConnectionInfo) {
            this.ecsChannelConnectionInfo = ecsChannelConnectionInfo;
            return this;
        }

        public Builder kvChannelConnectionInfo(ServerConnectionInfo kvChannelConnectionInfo) {
            this.kvChannelConnectionInfo = kvChannelConnectionInfo;
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

        public Builder readRanges(List<HashRange> readRanges) {
            this.readRanges = readRanges;
            return this;
        }

        public StorageNode build() {
            return new StorageNode(this);
        }
    }
}
