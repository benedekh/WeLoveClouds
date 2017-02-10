package weloveclouds.loadbalancer.services;

import java.util.List;

import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForReadingException;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForWritingException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2017-01-27.
 */
public interface IDistributedSystemAccessService {
    ServiceStatus getServiceStatus();

    List<StorageNode> getParticipatingNodes();

    StorageNode getNodeFrom(String name);

    RingTopology<StorageNode> getTopology();

    RingMetadata getRingMetadata();

    void removeParticipatingNode(StorageNode node);

    void updateServiceHealthWith(NodeHealthInfos nodeHealthInfos);

    void updateServiceTopologyWith(RingTopology<StorageNode> ringTopology);

    void updateServiceRingMetadataWith(RingMetadata ringMetadata);

    StorageNode getReadServerFor(String key)
            throws UnableToFindServerResponsibleForReadingException;

    StorageNode getWriteServerFor(String key) throws UnableToFindServerResponsibleForWritingException;

    void initializeServiceWith(List<StorageNode> initialNodes);
}
