package weloveclouds.server.services;

import static weloveclouds.server.services.models.DataAccessServiceStatus.STOPPED;
import static weloveclouds.server.utils.monitoring.KVServerMonitoringUtils.incrementCounter;
import static weloveclouds.server.utils.monitoring.KVServerMonitoringUtils.recordExecutionTime;
import static weloveclouds.server.utils.monitoring.MonitoringMetricConstants.ERROR;
import static weloveclouds.server.utils.monitoring.MonitoringMetricConstants.GET_COMMAND_NAME;
import static weloveclouds.server.utils.monitoring.MonitoringMetricConstants.KVSTORE_MODULE_NAME;
import static weloveclouds.server.utils.monitoring.MonitoringMetricConstants.NOT_RESPONSIBLE;
import static weloveclouds.server.utils.monitoring.MonitoringMetricConstants.PUT_COMMAND_NAME;
import static weloveclouds.server.utils.monitoring.MonitoringMetricConstants.REMOVE_COMMAND_NAME;
import static weloveclouds.server.utils.monitoring.MonitoringMetricConstants.SUCCESS;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import weloveclouds.client.utils.CustomStringJoiner;
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
            incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, ERROR);
            LOGGER.error("Put request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                try {
                    checkIfKeyIsManagedByServer(entry.getKey());
                } catch (KeyIsNotManagedByServiceException ex) {
                    incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, NOT_RESPONSIBLE);
                    throw ex;
                }

                try {
                    Instant start = Instant.now();
                    PutType putType = super.putEntry(entry);
                    recordExecutionTime(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME,
                            new Duration(start, Instant.now()));
                    incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, SUCCESS);
                    return putType;
                } catch (StorageException ex) {
                    incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, ERROR);
                    throw ex;
                }
            case STOPPED:
                incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, ERROR);
                LOGGER.error(
                        "Put request is rejected, because the data access service is stopped.");
                throw new ServiceIsStoppedException();
            case WRITELOCK_ACTIVE:
                incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, ERROR);
                LOGGER.error(
                        "Put request is rejected, because write lock is active on the data access service.");
                throw new WriteLockIsActiveException();
            default:
                incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, ERROR);
                throw new StorageException("Unrecognized service status.");
        }
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        if (!isServiceInitialized()) {
            incrementCounter(KVSTORE_MODULE_NAME, GET_COMMAND_NAME, ERROR);
            LOGGER.error("Get request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
            case WRITELOCK_ACTIVE:
                try {
                    checkIfKeyIsManagedByServer(key);
                } catch (KeyIsNotManagedByServiceException ex) {
                    incrementCounter(KVSTORE_MODULE_NAME, GET_COMMAND_NAME, NOT_RESPONSIBLE);
                    throw ex;
                }

                try {
                    Instant start = Instant.now();
                    String value = super.getValue(key);
                    recordExecutionTime(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME,
                            new Duration(start, Instant.now()));
                    incrementCounter(KVSTORE_MODULE_NAME, GET_COMMAND_NAME, SUCCESS);
                    return value;
                } catch (StorageException ex) {
                    incrementCounter(KVSTORE_MODULE_NAME, GET_COMMAND_NAME, ERROR);
                    throw ex;
                }
            case STOPPED:
                incrementCounter(KVSTORE_MODULE_NAME, GET_COMMAND_NAME, ERROR);
                LOGGER.error(
                        "Get request is rejected, because the data access service is stopped.");
                throw new ServiceIsStoppedException();
            default:
                incrementCounter(KVSTORE_MODULE_NAME, GET_COMMAND_NAME, ERROR);
                throw new StorageException("Unrecognized service status.");
        }
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        if (!isServiceInitialized()) {
            incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, ERROR);
            LOGGER.error("Remove request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                try {
                    checkIfKeyIsManagedByServer(key);
                } catch (KeyIsNotManagedByServiceException ex) {
                    incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, NOT_RESPONSIBLE);
                    throw ex;
                }

                try {
                    Instant start = Instant.now();
                    super.removeEntry(key);
                    recordExecutionTime(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME,
                            new Duration(start, Instant.now()));
                    incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, SUCCESS);
                } catch (StorageException ex) {
                    incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, ERROR);
                    throw ex;
                }
                break;
            case STOPPED:
                incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, ERROR);
                LOGGER.error(
                        "Remove request is rejected, because the data access service is stopped.");
                throw new ServiceIsStoppedException();
            case WRITELOCK_ACTIVE:
                incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, ERROR);
                LOGGER.error(
                        "Remove request is rejected, because write lock is active on the data access service.");
                throw new WriteLockIsActiveException();
            default:
                incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, ERROR);
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
