package weloveclouds.server.services;

import static weloveclouds.server.services.models.DataAccessServiceStatus.STOPPED;
import static weloveclouds.server.utils.monitoring.KVServerMonitoringMetricUtils.incrementCounter;
import static weloveclouds.server.utils.monitoring.KVServerMonitoringMetricUtils.recordExecutionTime;
import static weloveclouds.server.utils.monitoring.MonitoringMetricConstants.ERROR;
import static weloveclouds.server.utils.monitoring.MonitoringMetricConstants.EXEC_TIME;
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
import weloveclouds.server.models.replication.HashRangeWithRole;
import weloveclouds.server.models.replication.HashRangesWithRoles;
import weloveclouds.server.models.replication.Role;
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
    private volatile HashRangesWithRoles rangesManagedByServer;

    public MovableDataAccessService(KVCache cache, MovablePersistentStorage persistentStorage) {
        super(cache, persistentStorage);
        this.movablePersistentStorage = persistentStorage;
        this.servicePreviousStatus = STOPPED;
        this.serviceRecentStatus = STOPPED;
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        return putEntry(entry, true);
    }

    @Override
    public synchronized PutType putEntryWithoutAuthorization(KVEntry entry)
            throws StorageException {
        return putEntry(entry, false);
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
                    checkIfKeyIsManagedByServer(key, Role.REPLICA);
                } catch (KeyIsNotManagedByServiceException ex) {
                    incrementCounter(KVSTORE_MODULE_NAME, GET_COMMAND_NAME, NOT_RESPONSIBLE);
                    throw ex;
                }

                try {
                    Instant start = Instant.now();
                    String value = super.getValue(key);
                    recordExecutionTime(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, EXEC_TIME,
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
        removeEntry(key, true);
    }

    @Override
    public synchronized void removeEntryWithoutAuthorization(String key) throws StorageException {
        removeEntry(key, false);
    }

    @Override
    public synchronized void putEntries(MovableStorageUnits fromStorageUnits)
            throws StorageException {
        if (!isServiceInitialized()) {
            LOGGER.error("Put entries request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        LOGGER.debug("Putting entries from other storage units started.");
        movablePersistentStorage.putEntries(fromStorageUnits);
        LOGGER.debug("Putting entries from other storage units finished.");
    }

    @Override
    public synchronized MovableStorageUnits filterEntries(HashRange range)
            throws UninitializedServiceException {
        if (!isServiceInitialized()) {
            LOGGER.error("Filter entries request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        LOGGER.debug(CustomStringJoiner.join(" ", "Filtering entries in range:", range.toString()));
        return movablePersistentStorage.filterEntries(range);
    }

    @Override
    public synchronized void removeEntries(HashRange range) throws StorageException {
        if (!isServiceInitialized()) {
            LOGGER.error("Remove entries request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        LOGGER.debug(CustomStringJoiner.join(" ", "Removing entries in range:", range.toString()));
        movablePersistentStorage.removeEntries(range);
        LOGGER.debug("Removing entries finished.");
    }

    @Override
    public synchronized void defragment() throws UninitializedServiceException {
        if (!isServiceInitialized()) {
            LOGGER.error("Defragmentation request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        LOGGER.debug("Starting defragmentation.");
        movablePersistentStorage.defragment();
        LOGGER.debug("Defragmentation finished.");
    }

    @Override
    public synchronized void setServiceStatus(DataAccessServiceStatus serviceNewStatus)
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
    public synchronized void setRingMetadata(RingMetadata ringMetadata) {
        this.ringMetadata = ringMetadata;
    }

    @Override
    public synchronized RingMetadata getRingMetadata() {
        return ringMetadata;
    }

    @Override
    public synchronized void setManagedHashRanges(HashRangesWithRoles rangesManagedByServer) {
        this.rangesManagedByServer = rangesManagedByServer;
    }

    @Override
    public synchronized boolean isServiceInitialized() {
        return ringMetadata != null && rangesManagedByServer != null;
    }

    /**
     * Puts the respective entry into the storage
     * 
     * @param entry that has to be put in the storage
     * @param coordinatorRoleIsExpected if it has to be checked that the server really handles that
     *        key
     * @throws StorageException if any error occurs
     */
    protected PutType putEntry(KVEntry entry, boolean coordinatorRoleIsExpected)
            throws StorageException {
        if (!isServiceInitialized()) {
            incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, ERROR);
            LOGGER.error("Put request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                if (coordinatorRoleIsExpected) {
                    try {
                        checkIfKeyIsManagedByServer(entry.getKey(), Role.COORDINATOR);
                    } catch (KeyIsNotManagedByServiceException ex) {
                        incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, NOT_RESPONSIBLE);
                        throw ex;
                    }
                }

                try {
                    Instant start = Instant.now();
                    PutType putType = super.putEntry(entry);
                    recordExecutionTime(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, EXEC_TIME,
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

    /**
     * Removes the respective key along with the stored value from the storage
     * 
     * @param key the key of the entry that shall be removed
     * @param coordinatorRoleIsExpected if it has to be checked that the server really handles that
     *        key
     * @throws StorageException if any error occurs
     */
    protected void removeEntry(String key, boolean coordinatorRoleIsExpected)
            throws StorageException {
        if (!isServiceInitialized()) {
            incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, ERROR);
            LOGGER.error("Remove request while the data acess service was uninitialized.");
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                if (coordinatorRoleIsExpected) {
                    try {
                        checkIfKeyIsManagedByServer(key, Role.COORDINATOR);
                    } catch (KeyIsNotManagedByServiceException ex) {
                        incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, NOT_RESPONSIBLE);
                        throw ex;
                    }
                }

                try {
                    Instant start = Instant.now();
                    super.removeEntry(key);
                    recordExecutionTime(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, EXEC_TIME,
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

    /**
     * @param key the key whose hash value is expected to be handled by the server
     * @param expectedRole the expected type of the role which belongs to the hash range (in which
     *        the hash value of the key is)
     * @throws KeyIsNotManagedByServiceException if the referred key's hash value is not managed by
     *         this server.
     */
    private void checkIfKeyIsManagedByServer(String key, Role expectedRole)
            throws KeyIsNotManagedByServiceException {
        if (rangesManagedByServer == null || !keyIsManagedByServer(key, expectedRole)) {
            LOGGER.debug(
                    CustomStringJoiner.join("", "Key (", key, ") is not managed by the server."));
            throw new KeyIsNotManagedByServiceException();
        }
    }

    /**
     * @param key the key whose hash value is expected to be handled by the server
     * @param expectedRole the expected type of the role which belongs to the hash range (in which
     *        the hash value of the key is)
     * @return true if the key's hash value is managed by the server and the role for that range is
     *         the same as the expected one
     */
    private boolean keyIsManagedByServer(String key, Role expectedRole) {
        for (HashRangeWithRole rangeWithRole : rangesManagedByServer.getRangesWithRoles()) {
            boolean keyIsContainedInTheRange =
                    rangeWithRole.getHashRange().contains(HashingUtil.getHash(key));
            boolean roleIsTheExpected = expectedRole.equals(rangeWithRole.getRole());
            if (keyIsContainedInTheRange && roleIsTheExpected) {
                return true;
            } else if (keyIsContainedInTheRange && !roleIsTheExpected) {
                return false;
            }
        }
        return false;
    }

}
