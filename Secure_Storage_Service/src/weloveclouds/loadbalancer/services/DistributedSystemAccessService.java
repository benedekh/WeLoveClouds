package weloveclouds.loadbalancer.services;

import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForReadingException;
import weloveclouds.ecs.exceptions.distributedSystem.UnableToFindServerResponsibleForWritingException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.services.DistributedService;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.status.ServiceStatus.INITIALIZED;

/**
 * Created by Benoit on 2016-12-03.
 */
@Singleton
public class DistributedSystemAccessService {
    private static final Logger LOGGER = Logger.getLogger(DistributedSystemAccessService.class);
    private static final int FIRST = 0;
    private DistributedService distributedService;
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    public DistributedSystemAccessService() {
        distributedService = new DistributedService();
    }

    public ServiceStatus getServiceStatus() {
        return distributedService.getStatus();
    }

    public void updateServiceHealthWith(NodeHealthInfos nodeHealthInfos) {
        try {
            reentrantReadWriteLock.writeLock().lock();
            LOGGER.debug("Updating health infos of: " + nodeHealthInfos.getNodeName());
            distributedService.getNodeFrom(nodeHealthInfos.getNodeName())
                    .updateHealthInfos(nodeHealthInfos);
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    public void updateServiceTopologyWith(RingTopology<StorageNode> ringTopology) {
        try {
            if (distributedService.getStatus() != INITIALIZED) {
                reentrantReadWriteLock.writeLock().lock();
                distributedService.initializeWith(ringTopology);
                LOGGER.debug("Loadbalancer topology initialized");
            } else {
                reentrantReadWriteLock.writeLock().lock();
                distributedService.updateTopologyWith(ringTopology);
                LOGGER.debug("Loadbalancer topology updated");
            }
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    public void updateServiceRingMetadataWith(RingMetadata ringMetadata) {
        try {
            reentrantReadWriteLock.writeLock().lock();
            distributedService.updateRingMetadataWith(ringMetadata);
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    public StorageNode getReadServerFor(String key)
            throws UnableToFindServerResponsibleForReadingException {
        LOGGER.debug("Getting read server for key: " + key);
        StorageNode healthiestNode = null;
        try {
            reentrantReadWriteLock.readLock().lock();
            healthiestNode = getHealthiestNodeFrom(
                    distributedService.getResponsibleForReadingOf(HashingUtils.getHash(key)));
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
        LOGGER.debug("Read server for key: " + key + "is: " + healthiestNode.toString());
        return healthiestNode;
    }

    public StorageNode getWriteServerFor(String key) throws UnableToFindServerResponsibleForWritingException {
        LOGGER.debug("Getting write server for key: " + key);
        StorageNode writeServer = null;
        try {
            reentrantReadWriteLock.readLock().lock();
            writeServer = distributedService.getResponsibleForWritingOf(HashingUtils.getHash(key));
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
        LOGGER.debug("Write server for key: " + key + "is: " + writeServer.toString());
        return writeServer;
    }

    private StorageNode getHealthiestNodeFrom(List<StorageNode> storageNodes) {
        try {
            reentrantReadWriteLock.readLock().lock();
            Collections.sort(storageNodes, new Comparator<StorageNode>() {
                @Override
                public int compare(StorageNode node1, StorageNode node2) {
                    return node1.getHealthInfos().compareTo(node2.getHealthInfos());
                }
            });
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
        return storageNodes.get(FIRST);
    }
}
