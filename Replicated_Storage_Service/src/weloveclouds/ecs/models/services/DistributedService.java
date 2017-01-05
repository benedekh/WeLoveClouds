package weloveclouds.ecs.models.services;

import static weloveclouds.commons.status.ServiceStatus.INITIALIZED;
import static weloveclouds.commons.status.ServiceStatus.UNINITIALIZED;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForReadingException;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForWritingException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.ecs.utils.RingMetadataHelper;

/**
 * Created by Benoit on 2016-11-30.
 */
public class DistributedService {
    private RingTopology<StorageNode> topology;
    private RingMetadata ringMetadata;
    private ServiceStatus status;

    public DistributedService() {
        this.topology = new RingTopology<>();
        this.ringMetadata = new RingMetadata();
        this.status = UNINITIALIZED;
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
        return this.topology;
    }

    public List<StorageNode> getParticipatingNodes() {
        return this.topology.getNodes();
    }

    public StorageNode getNodeFrom(String name) {
        StorageNode storageNode = null;

        for (StorageNode node : getParticipatingNodes()) {
            if (node.getName().equals(name)) {
                storageNode = node;
                break;
            }
        }
        return storageNode;
    }

    public StorageNode getNodeFrom(ServerConnectionInfo serverConnectionInfo) {
        StorageNode storageNode = null;

        for (StorageNode node : getParticipatingNodes()) {
            if (node.getServerConnectionInfo().equals(serverConnectionInfo)) {
                storageNode = node;
                break;
            }
        }
        return storageNode;
    }

    public void updateTopologyWith(RingTopology newTopology) {
        if (newTopology != null) {
            topology.updateWith(newTopology);
            updateRingMetadataFrom(topology);
        }
    }

    public void initializeWith(List<StorageNode> initialNodes) {
        topology.updateWith(RingMetadataHelper.computeRingOrder(initialNodes));
        updateRingMetadataFrom(topology);
        status = INITIALIZED;
    }

    public void initializeWith(RingTopology<StorageNode> newTopology) {
        topology.updateWith(newTopology);
        updateRingMetadataFrom(topology);
        status = INITIALIZED;
    }

    public void updateRingMetadataFrom(RingTopology<StorageNode> ringTopology) {
        HashRange previousRange = null;

        for (StorageNode node : ringTopology.getNodes()) {
            HashRange hashRange;
            int ringPosition = ringTopology.getRingPositionOf(node);

            hashRange = RingMetadataHelper.computeHashRangeForNodeBasedOnRingPosition(ringPosition,
                    ringTopology.getNumberOfNodes(), node.getHashKey(), previousRange);
            node.setHashRange(hashRange);

            // TODO Can you add a TODO, cause I'll have to modify this to add the range again
            ringMetadata.addRangeInfo(new RingMetadataPart.Builder()
                    .connectionInfo(node.getServerConnectionInfo()).build());

            previousRange = hashRange;
        }
    }
}
