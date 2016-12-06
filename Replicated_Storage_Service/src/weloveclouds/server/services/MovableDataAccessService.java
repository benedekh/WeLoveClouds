package weloveclouds.server.services;

import static weloveclouds.commons.monitoring.statsd.IStatsdClient.SINGLE_EVENT;
import static weloveclouds.server.services.models.DataAccessServiceStatus.STOPPED;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import static app_kvServer.KVServer.*;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.monitoring.models.Metric;
import static weloveclouds.commons.monitoring.models.Service.*;
import weloveclouds.commons.monitoring.statsd.IStatsdClient;
import weloveclouds.commons.monitoring.statsd.StatsdClientFactory;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.RingMetadataSerializer;
import weloveclouds.server.services.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.exceptions.ServiceIsStoppedException;
import weloveclouds.server.services.exceptions.UninitializedServiceException;
import weloveclouds.server.services.exceptions.WriteLockIsActiveException;
import weloveclouds.server.services.models.DataAccessServiceStatus;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.MovablePersistentStorage;
import weloveclouds.server.store.PutType;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * An implementation of {@link IMovableDataAccessService} whose underlying storage units can be
 * moved.
 * 
 * @author Benedek
 */
public class MovableDataAccessService extends DataAccessService
        implements IMovableDataAccessService {

    private static final Logger LOGGER = Logger.getLogger(MovableDataAccessService.class);
    private static final IStatsdClient STATSD_CLIENT =
            StatsdClientFactory.createStatdClientFromEnvironment();

    private MovablePersistentStorage movablePersistentStorage;

    private DataAccessServiceStatus servicePreviousStatus;
    private volatile DataAccessServiceStatus serviceRecentStatus;

    private volatile RingMetadata ringMetadata;
    private volatile HashRange rangeManagedByServer;

    private ISerializer<String, RingMetadata> ringMetadatSerializer = new RingMetadataSerializer();

    public MovableDataAccessService(KVCache cache, MovablePersistentStorage persistentStorage) {
        super(cache, persistentStorage);
        this.movablePersistentStorage = persistentStorage;
        this.servicePreviousStatus = STOPPED;
        this.serviceRecentStatus = STOPPED;
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        if (!isServiceInitialized()) {
            LOGGER.error("Put request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                try {
                    checkIfKeyIsManagedByServer(entry.getKey());
                } catch (KeyIsNotManagedByServiceException ex) {
                    STATSD_CLIENT.incrementCounter(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "put", "not_responsible"))
                            .build(), SINGLE_EVENT);
                    throw ex;
                }


                Instant start = Instant.now();
                try {
                    PutType putType = super.putEntry(entry);
                    STATSD_CLIENT.incrementCounter(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "put", "success")).build(),
                            SINGLE_EVENT);
                    return putType;
                } catch (StorageException ex) {
                    STATSD_CLIENT.incrementCounter(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "put", "error")).build(),
                            SINGLE_EVENT);
                    throw ex;
                } finally {
                    Instant end = Instant.now();
                    STATSD_CLIENT.recordExecutionTime(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "exec_time", "put"))
                            .build(), new Duration(start, end));
                }
            case STOPPED:
                LOGGER.error(
                        "Put request is rejected, because the data access service is stopped.");
                throw new ServiceIsStoppedException();
            case WRITELOCK_ACTIVE:
                LOGGER.error(
                        "Put request is rejected, because write lock is active on the data access service.");
                throw new WriteLockIsActiveException();
            default:
                throw new StorageException("Unrecognized service status.");
        }
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        if (!isServiceInitialized()) {
            LOGGER.error("Get request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
            case WRITELOCK_ACTIVE:
                try {
                    checkIfKeyIsManagedByServer(key);
                } catch (KeyIsNotManagedByServiceException ex) {
                    STATSD_CLIENT.incrementCounter(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "get", "not_responsible"))
                            .build(), SINGLE_EVENT);
                    throw ex;
                }

                Instant start = Instant.now();
                try {
                    String value = super.getValue(key);
                    STATSD_CLIENT.incrementCounter(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "get", "success")).build(),
                            SINGLE_EVENT);
                    return value;
                } catch (StorageException ex) {
                    STATSD_CLIENT.incrementCounter(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "get", "error")).build(),
                            SINGLE_EVENT);
                    throw ex;
                } finally {
                    Instant end = Instant.now();
                    STATSD_CLIENT.recordExecutionTime(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "exec_time", "get"))
                            .build(), new Duration(start, end));
                }
            case STOPPED:
                LOGGER.error(
                        "Get request is rejected, because the data access service is stopped.");
                throw new ServiceIsStoppedException();
            default:
                throw new StorageException("Unrecognized service status.");
        }
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        if (!isServiceInitialized()) {
            LOGGER.error("Remove request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                try {
                    checkIfKeyIsManagedByServer(key);
                } catch (KeyIsNotManagedByServiceException ex) {
                    STATSD_CLIENT
                            .incrementCounter(
                                    new Metric.Builder()
                                            .service(KV_SERVER).name(Arrays.asList(SERVER_NAME,
                                                    "kvstore", "remove", "not_responsible"))
                                            .build(),
                                    SINGLE_EVENT);
                    throw ex;
                }

                Instant start = Instant.now();
                try {
                    super.removeEntry(key);
                    STATSD_CLIENT.incrementCounter(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "remove", "success"))
                            .build(), SINGLE_EVENT);
                } catch (StorageException ex) {
                    STATSD_CLIENT.incrementCounter(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "remove", "error")).build(),
                            SINGLE_EVENT);
                    throw ex;
                } finally {
                    Instant end = Instant.now();
                    STATSD_CLIENT.recordExecutionTime(new Metric.Builder().service(KV_SERVER)
                            .name(Arrays.asList(SERVER_NAME, "kvstore", "exec_time", "remove"))
                            .build(), new Duration(start, end));
                }
                break;
            case STOPPED:
                LOGGER.error(
                        "Remove request is rejected, because the data access service is stopped.");
                throw new ServiceIsStoppedException();
            case WRITELOCK_ACTIVE:
                LOGGER.error(
                        "Remove request is rejected, because write lock is active on the data access service.");
                throw new WriteLockIsActiveException();
            default:
                throw new StorageException("Unrecognized service status.");
        }
    }

    @Override
    public void putEntries(MovableStorageUnits fromStorageUnits) throws StorageException {
        if (!isServiceInitialized()) {
            LOGGER.error("Put entries request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        LOGGER.debug("Putting entries from other storage units started.");
        movablePersistentStorage.putEntries(fromStorageUnits);
        LOGGER.debug("Putting entries from other storage units finished.");
    }

    @Override
    public MovableStorageUnits filterEntries(HashRange range) throws UninitializedServiceException {
        if (!isServiceInitialized()) {
            LOGGER.error("Filter entries request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        LOGGER.debug(CustomStringJoiner.join(" ", "Filtering entries in range:", range.toString()));
        return movablePersistentStorage.filterEntries(range);
    }

    @Override
    public void removeEntries(HashRange range) throws StorageException {
        if (!isServiceInitialized()) {
            LOGGER.error("Remove entries request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        LOGGER.debug(CustomStringJoiner.join(" ", "Removing entries in range:", range.toString()));
        movablePersistentStorage.removeEntries(range);
        LOGGER.debug("Removing entries finished.");
    }

    @Override
    public void defragment() throws UninitializedServiceException {
        if (!isServiceInitialized()) {
            LOGGER.error("Defragmentation request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        LOGGER.debug("Starting defragmentation.");
        movablePersistentStorage.defragment();
        LOGGER.debug("Defragmentation finished.");
    }

    @Override
    public void setServiceStatus(DataAccessServiceStatus serviceNewStatus)
            throws UninitializedServiceException {
        if (!isServiceInitialized()) {
            LOGGER.error(
                    "Set service status request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceNewStatus) {
            case WRITELOCK_INACTIVE:
                serviceRecentStatus = servicePreviousStatus;
                break;
            case STARTED:
            case STOPPED:
                serviceRecentStatus = serviceNewStatus;
                servicePreviousStatus = serviceRecentStatus;
                break;
            case WRITELOCK_ACTIVE:
                serviceRecentStatus = serviceNewStatus;
        }

        LOGGER.debug(CustomStringJoiner.join(" ", "Recent service status is:",
                serviceRecentStatus.toString()));
    }

    @Override
    public void setRingMetadata(RingMetadata ringMetadata) {
        this.ringMetadata = ringMetadata;
    }

    @Override
    public void setManagedHashRange(HashRange rangeManagedByServer) {
        this.rangeManagedByServer = rangeManagedByServer;
    }

    @Override
    public boolean isServiceInitialized() {
        return ringMetadata != null && rangeManagedByServer != null;
    }

    private void checkIfKeyIsManagedByServer(String key) throws KeyIsNotManagedByServiceException {
        if (rangeManagedByServer == null
                || !rangeManagedByServer.contains(HashingUtil.getHash(key))) {
            LOGGER.debug(
                    CustomStringJoiner.join("", "Key (", key, ") is not managed by the server."));
            throw new KeyIsNotManagedByServiceException(
                    ringMetadatSerializer.serialize(ringMetadata));
        }

    }

}
