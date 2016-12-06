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
import static weloveclouds.server.utils.statd.StatdMetricConstants.*;

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
            statdIncrementCounter(PUT_COMMAND_NAME, ERROR);
            LOGGER.error("Put request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                try {
                    checkIfKeyIsManagedByServer(entry.getKey());
                } catch (KeyIsNotManagedByServiceException ex) {
                    statdIncrementCounter(PUT_COMMAND_NAME, NOT_RESPONSIBLE);
                    throw ex;
                }

                try {
                    Instant start = Instant.now();
                    PutType putType = super.putEntry(entry);
                    statdRecordExecutionTime(PUT_COMMAND_NAME, new Duration(start, Instant.now()));
                    statdIncrementCounter(PUT_COMMAND_NAME, SUCCESS);
                    return putType;
                } catch (StorageException ex) {
                    statdIncrementCounter(PUT_COMMAND_NAME, ERROR);
                    throw ex;
                }
            case STOPPED:
                statdIncrementCounter(PUT_COMMAND_NAME, ERROR);
                LOGGER.error(
                        "Put request is rejected, because the data access service is stopped.");
                throw new ServiceIsStoppedException();
            case WRITELOCK_ACTIVE:
                statdIncrementCounter(PUT_COMMAND_NAME, ERROR);
                LOGGER.error(
                        "Put request is rejected, because write lock is active on the data access service.");
                throw new WriteLockIsActiveException();
            default:
                statdIncrementCounter(PUT_COMMAND_NAME, ERROR);
                throw new StorageException("Unrecognized service status.");
        }
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        if (!isServiceInitialized()) {
            statdIncrementCounter(GET_COMMAND_NAME, ERROR);
            LOGGER.error("Get request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
            case WRITELOCK_ACTIVE:
                try {
                    checkIfKeyIsManagedByServer(key);
                } catch (KeyIsNotManagedByServiceException ex) {
                    statdIncrementCounter(GET_COMMAND_NAME, NOT_RESPONSIBLE);
                    throw ex;
                }

                try {
                    Instant start = Instant.now();
                    String value = super.getValue(key);
                    statdRecordExecutionTime(PUT_COMMAND_NAME, new Duration(start, Instant.now()));
                    statdIncrementCounter(GET_COMMAND_NAME, SUCCESS);
                    return value;
                } catch (StorageException ex) {
                    statdIncrementCounter(GET_COMMAND_NAME, ERROR);
                    throw ex;
                }
            case STOPPED:
                statdIncrementCounter(GET_COMMAND_NAME, ERROR);
                LOGGER.error(
                        "Get request is rejected, because the data access service is stopped.");
                throw new ServiceIsStoppedException();
            default:
                statdIncrementCounter(GET_COMMAND_NAME, ERROR);
                throw new StorageException("Unrecognized service status.");
        }
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        if (!isServiceInitialized()) {
            statdIncrementCounter(REMOVE_COMMAND_NAME, ERROR);
            LOGGER.error("Remove request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                try {
                    checkIfKeyIsManagedByServer(key);
                } catch (KeyIsNotManagedByServiceException ex) {
                    statdIncrementCounter(REMOVE_COMMAND_NAME, NOT_RESPONSIBLE);
                    throw ex;
                }

                try {
                    Instant start = Instant.now();
                    super.removeEntry(key);
                    statdRecordExecutionTime(REMOVE_COMMAND_NAME,
                            new Duration(start, Instant.now()));
                    statdIncrementCounter(REMOVE_COMMAND_NAME, SUCCESS);
                } catch (StorageException ex) {
                    statdIncrementCounter(REMOVE_COMMAND_NAME, ERROR);
                    throw ex;
                }
                break;
            case STOPPED:
                statdIncrementCounter(REMOVE_COMMAND_NAME, ERROR);
                LOGGER.error(
                        "Remove request is rejected, because the data access service is stopped.");
                throw new ServiceIsStoppedException();
            case WRITELOCK_ACTIVE:
                statdIncrementCounter(REMOVE_COMMAND_NAME, ERROR);
                LOGGER.error(
                        "Remove request is rejected, because write lock is active on the data access service.");
                throw new WriteLockIsActiveException();
            default:
                statdIncrementCounter(REMOVE_COMMAND_NAME, ERROR);
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

    /**
     * Increments a statd counter whose metric name ends with the referred command and status names.
     */
    private void statdIncrementCounter(String command, String status) {
        STATSD_CLIENT.incrementCounter(
                new Metric.Builder().service(KV_SERVER)
                        .name(Arrays.asList(SERVER_NAME, "kvstore", command, status)).build(),
                SINGLE_EVENT);
    }

    /**
     * Records a statd execution time whose metric name ends with the referred command name and
     * execution time duration.
     */
    private void statdRecordExecutionTime(String command, Duration executionTime) {
        STATSD_CLIENT.recordExecutionTime(
                new Metric.Builder().service(KV_SERVER)
                        .name(Arrays.asList(SERVER_NAME, "kvstore", "exec_time", command)).build(),
                executionTime);
    }

    /**
     * @throws KeyIsNotManagedByServiceException if the referred key's hash value is not managed by
     *         this server.
     */
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
