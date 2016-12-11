package weloveclouds.ecs.models.services;

import static weloveclouds.commons.status.ServiceStatus.INITIALIZED;
import static weloveclouds.commons.status.ServiceStatus.UNINITIALIZED;

import java.util.List;

import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.ecs.utils.RingMetadataHelper;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;

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

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public RingMetadata getRingMetadata() {
        return ringMetadata;
    }

    public RingTopology<StorageNode> getTopology() {
        return this.topology;
    }

    public List<StorageNode> getParticipatingNodes() {
        return this.topology.getNodes();
    }

    public void updateTopologyWith(RingTopology newTopology) {
        topology.updateWith(newTopology);
        updateRingMetadataFrom(topology);
    }

    public void initializeWith(List<StorageNode> initialNodes) {
        topology.updateWith(RingMetadataHelper.computeRingOrder(initialNodes));
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
