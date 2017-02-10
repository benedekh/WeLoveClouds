package weloveclouds.ecs.models.services;

import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import static weloveclouds.commons.status.ServiceStatus.INITIALIZED;
import static weloveclouds.commons.status.ServiceStatus.UNINITIALIZED;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForReadingException;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForWritingException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.ecs.utils.RingMetadataHelper;

/**
 * Created by Benoit on 2016-11-30.
 */
@Singleton
public class DistributedService {
    private static final Logger LOGGER = Logger.getLogger(DistributedService.class);
    private static final int TWO_REPLICAS = 2;
    private RingTopology<StorageNode> topology;
    private RingMetadata ringMetadata;
    private ServiceStatus status;

    public DistributedService() {
        this.topology = new RingTopology<>();
        this.ringMetadata = new RingMetadata();
        this.status = UNINITIALIZED;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public RingMetadata getRingMetadata() {
        return ringMetadata;
    }

    public void updateRingMetadataWith(RingMetadata ringMetadata) {
        if (ringMetadata != null) {
            this.ringMetadata = ringMetadata;
        }
    }

    public RingTopology<StorageNode> getTopology() {
        return topology;
    }

    public List<StorageNode> getParticipatingNodes() {
        return topology.getNodes();
    }

    public void removeParticipatingNode(StorageNode node){
        topology.removeNodes(node);
    }

    public StorageNode getNodeFrom(String name) {
        StorageNode storageNode = null;

        for (StorageNode node : getParticipatingNodes()) {
            if (node.getName().equals(name)) {
                storageNode = node;
                break;
            }
        }
        LOGGER.debug("Retrieved node named: " + name + " " + storageNode.toString());
        return storageNode;
    }

    public void initializeWith(List<StorageNode> initialNodes) {
        topology.updateWith(RingMetadataHelper.computeRingOrder(initialNodes));
        computeAndUpdateNodesRangesFrom(topology);
        updateRingMetadataFrom(topology);
        status = INITIALIZED;
    }

    public void initializeWith(RingTopology<StorageNode> newTopology) {
        topology.updateWith(newTopology);
        computeAndUpdateNodesRangesFrom(topology);
        updateRingMetadataFrom(topology);
        status = INITIALIZED;
    }

    public List<StorageNode> getResponsibleForReadingOf(Hash hash) throws
            UnableToFindServerResponsibleForReadingException {
        List<StorageNode> responsibles = new ArrayList<>();
        for (StorageNode node : getParticipatingNodes()) {
            if (node.isWriteResponsibleOf(hash)) {
                responsibles.add(node);
                responsibles.addAll(node.getReplicas());
                return responsibles;
            }
        }
        throw new UnableToFindServerResponsibleForReadingException(hash);
    }

    public StorageNode getResponsibleForWritingOf(Hash hash) throws
            UnableToFindServerResponsibleForWritingException {
        for (StorageNode node : getParticipatingNodes()) {
            if (node.isWriteResponsibleOf(hash)) {
                return node;
            }
        }
        throw new UnableToFindServerResponsibleForWritingException(hash);
    }

    public void updateTopologyWith(RingTopology newTopology) {
        if (newTopology != null) {
            topology.updateWith(newTopology);
            resetNodesReplicasAndReadRangesFrom(newTopology);
            computeAndUpdateNodesRangesFrom(topology);
            updateRingMetadataFrom(topology);
        }
    }

    private void resetNodesReplicasAndReadRangesFrom(RingTopology<StorageNode> ringTopology) {
        for (StorageNode node : ringTopology.getNodes()) {
            node.clearReadRanges();
            node.clearReplicas();
        }
    }

    private void computeAndUpdateNodesRangesFrom(RingTopology<StorageNode> ringTopology) {
        HashRange previousRange = null;

        for (StorageNode node : ringTopology.getNodes()) {
            HashRange hashRange;
            int ringPosition = ringTopology.getRingPositionOf(node);

            hashRange = RingMetadataHelper.computeHashRangeForNodeBasedOnRingPosition(ringPosition,
                    ringTopology.getNumberOfNodes(), node.getHashKey(), previousRange);
            node.setHashRange(hashRange);
            previousRange = hashRange;

            for (StorageNode replica : ringTopology.getReplicasOf(node, TWO_REPLICAS)) {
                replica.addReadRange(node.getHashRange());
                node.addReplica(replica);
            }
        }
    }

    private void updateRingMetadataFrom(RingTopology<StorageNode> ringTopology) {
        for (StorageNode node : ringTopology.getNodes()) {
            ringMetadata.addRangeInfo(new RingMetadataPart.Builder()
                    .connectionInfo(node.getServerConnectionInfo())
                    .writeRange(node.getHashRange())
                    .readRanges(new LinkedHashSet<>(node.getReadRanges()))
                    .build());
        }
    }
}
