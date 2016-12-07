package weloveclouds.loadbalancer.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindResponsibleForReadingException;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindResponsibleForWritingException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.services.DistributedService;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-03.
 */
public class DistributedSystemAccessService {
    private static final int FIRST = 0;
    private DistributedService distributedService;

    public void updateServiceHealthWith(NodeHealthInfos serverHealthInfos) {
        synchronized (distributedService) {
            distributedService.getNodeFrom(serverHealthInfos.getServerConnectionInfo())
                    .updateHealthInfos(serverHealthInfos);
        }
    }

    public void updateServiceRingMetadataWith(RingMetadata ringMetadata) {
        synchronized (distributedService) {
            distributedService.updateRingMetadataWith(ringMetadata);
        }
    }

    public StorageNode getReadServerFor(String key) throws UnableToFindResponsibleForReadingException {

        return getHealthiestNodeFrom(distributedService.getResponsibleForReadingOf(new Hash(key.getBytes())));
    }

    public StorageNode getWriteServerFor(String key) throws UnableToFindResponsibleForWritingException {
        return distributedService.getResponsibleForWritingOf(new Hash(key.getBytes()));
    }

    private StorageNode getHealthiestNodeFrom(List<StorageNode> storageNodes) {
        Collections.sort(storageNodes, new Comparator<StorageNode>() {
            @Override
            public int compare(StorageNode node1, StorageNode node2) {
                return node1.getHealthInfos().compareTo(node2.getHealthInfos());
            }
        });
        return storageNodes.get(FIRST);
    }
}
