package weloveclouds.server.services.datastore;

import static weloveclouds.server.monitoring.KVServerMonitoringMetricUtils.incrementCounter;
import static weloveclouds.server.monitoring.KVServerMonitoringMetricUtils.recordExecutionTime;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.ERROR;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.EXEC_TIME;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.GET_COMMAND_NAME;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.KVSTORE_MODULE_NAME;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.NOT_RESPONSIBLE;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.PUT_COMMAND_NAME;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.REMOVE_COMMAND_NAME;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.SUCCESS;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.services.datastore.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.datastore.exceptions.ServiceIsStoppedException;
import weloveclouds.server.services.datastore.exceptions.UninitializedServiceException;
import weloveclouds.server.services.datastore.exceptions.WriteLockIsActiveException;
import weloveclouds.server.services.datastore.models.DataAccessServiceStatus;
import weloveclouds.server.store.cache.KVCache;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.PutType;
import weloveclouds.server.store.storage.MovablePersistentStorage;

/**
 * An implementation of {@link IMovableDataAccessService} whose underlying storage units can be
 * moved.
 * 
 * @author Benedek
 */
public class MovableDataAccessService<E extends MovableDataAccessService.Builder<E>>
        extends DataAccessService implements IMovableDataAccessService {

    private static final Logger LOGGER = Logger.getLogger(MovableDataAccessService.class);

    private SimulatedMovableDataAccessService simulatedDataAccessService;
    private MovablePersistentStorage movablePersistentStorage;

    private volatile DataAccessServiceStatus servicePreviousStatus;
    private volatile DataAccessServiceStatus serviceRecentStatus;

    private Set<HashRange> readRanges;
    private volatile HashRange writeRange;
    private volatile RingMetadata ringMetadata;

    private ReentrantReadWriteLock configurationChangeLock;

    protected MovableDataAccessService(Builder<E> builder) {
        super(builder.cache, builder.persistentStorage);
        this.movablePersistentStorage = builder.persistentStorage;
        this.simulatedDataAccessService = builder.simulatedDataAccessService;
        this.servicePreviousStatus = DataAccessServiceStatus.STOPPED;
        this.serviceRecentStatus = DataAccessServiceStatus.STOPPED;
        this.readRanges = new HashSet<>();
        this.configurationChangeLock = new ReentrantReadWriteLock();
    }

    @Override
    public PutType putEntry(KVEntry entry) throws StorageException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            return putEntry(entry, true);
        }
    }

    @Override
    public PutType putEntryWithoutAuthorization(KVEntry entry) throws StorageException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            return putEntry(entry, false);
        }
    }

    @Override
    public String getValue(String key) throws StorageException, ValueNotFoundException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            if (!isServiceInitialized()) {
                incrementCounter(KVSTORE_MODULE_NAME, GET_COMMAND_NAME, ERROR);
                LOGGER.error("Get request while the data acess service was uninitialized.");
                throw new UninitializedServiceException();
            }
            switch (serviceRecentStatus) {
                case STARTED:
                case WRITELOCK_ACTIVE:
                    try {
                        checkIfServiceHasReadPrivilegeFor(key);
                    } catch (KeyIsNotManagedByServiceException ex) {
                        incrementCounter(KVSTORE_MODULE_NAME, GET_COMMAND_NAME, NOT_RESPONSIBLE);
                        throw ex;
                    }
                    try {
                        Instant start = Instant.now();
                        String value = super.getValue(key);
                        recordExecutionTime(KVSTORE_MODULE_NAME, GET_COMMAND_NAME, EXEC_TIME,
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
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            removeEntry(key, true);
        }
    }

    @Override
    public void removeEntryWithoutAuthorization(String key) throws StorageException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            removeEntry(key, false);
        }
    }

    @Override
    public void putEntries(Set<MovableStorageUnit> fromStorageUnits) throws StorageException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            if (!isServiceInitialized()) {
                LOGGER.error("Put entries request while the data acess service was uninitialized.");
                throw new UninitializedServiceException();
            }

            LOGGER.debug("Putting entries from other storage units started.");
            movablePersistentStorage.putEntries(fromStorageUnits);
            LOGGER.debug("Putting entries from other storage units finished.");
        }
    }

    @Override
    public Set<MovableStorageUnit> filterEntries(HashRange range)
            throws UninitializedServiceException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            if (!isServiceInitialized()) {
                LOGGER.error(
                        "Filter entries request while the data acess service was uninitialized.");
                throw new UninitializedServiceException();
            }

            LOGGER.debug(StringUtils.join(" ", "Filtering entries in range:", range));
            return movablePersistentStorage.filterEntries(range);
        }
    }

    @Override
    public void removeEntries(HashRange range) throws StorageException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            if (!isServiceInitialized()) {
                LOGGER.error(
                        "Remove entries request while the data acess service was uninitialized.");
                throw new UninitializedServiceException();
            }

            LOGGER.debug(StringUtils.join(" ", "Removing entries in range:", range));
            movablePersistentStorage.removeEntries(range);
            LOGGER.debug("Removing entries finished.");
        }
    }

    @Override
    public void defragment() throws UninitializedServiceException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            if (!isServiceInitialized()) {
                LOGGER.error(
                        "Defragmentation request while the data acess service was uninitialized.");
                throw new UninitializedServiceException();
            }

            LOGGER.debug("Starting defragmentation.");
            movablePersistentStorage.defragment();
            LOGGER.debug("Defragmentation finished.");
        }
    }

    @Override
    public void setServiceStatus(DataAccessServiceStatus serviceNewStatus)
            throws UninitializedServiceException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.writeLock())) {
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

            simulatedDataAccessService.setServiceStatus(serviceNewStatus);
            LOGGER.debug(StringUtils.join(" ", "Recent service status is:", serviceRecentStatus));
        }
    }

    @Override
    public DataAccessServiceStatus getServiceStatus() {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            return serviceRecentStatus;
        }
    }

    @Override
    public void setRingMetadata(RingMetadata ringMetadata) {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.writeLock())) {
            this.ringMetadata = ringMetadata;
            simulatedDataAccessService.setRingMetadata(ringMetadata);
        }
    }

    @Override
    public RingMetadata getRingMetadata() {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            return ringMetadata;
        }
    }

    @Override
    public void setManagedHashRanges(Set<HashRange> readRanges, HashRange writeRange) {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.writeLock())) {
            if (readRanges != null) {
                this.readRanges.clear();
                this.readRanges.addAll(readRanges);
            }
            if (writeRange != null) {
                this.readRanges.add(writeRange);
            }
            this.writeRange = writeRange;
            simulatedDataAccessService.setManagedHashRanges(readRanges, writeRange);
        }
    }

    @Override
    public boolean isServiceInitialized() {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            return ringMetadata != null && (!readRanges.isEmpty() || writeRange != null);
        }
    }

    @Override
    public SimulatedMovableDataAccessService getSimulatedDataAccessService() {
        return simulatedDataAccessService;
    }

    /**
     * Puts the respective entry into the storage
     * 
     * @param entry that has to be put in the storage
     * @param writePrivilegeIsExpected if the data access service is expected to have write
     *        privilege
     * @throws StorageException if any error occurs
     */
    protected PutType putEntry(KVEntry entry, boolean writePrivilegIsExpected)
            throws StorageException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            if (!isServiceInitialized()) {
                incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, ERROR);
                LOGGER.error("Put request while the data acess service was uninitialized.");
                throw new UninitializedServiceException();
            }

            switch (serviceRecentStatus) {
                case STARTED:
                    try {
                        if (writePrivilegIsExpected) {
                            checkIfServiceHasWritePrivilegeFor(entry.getKey());
                        } else {
                            checkIfServiceHasReadPrivilegeFor(entry.getKey());
                        }
                    } catch (KeyIsNotManagedByServiceException ex) {
                        incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, NOT_RESPONSIBLE);
                        throw ex;
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
    }

    /**
     * Removes the respective key along with the stored value from the storage
     * 
     * @param key the key of the entry that shall be removed
     * @param writePrivilegeIsExpected if the data access service is expected to have write
     *        privilege
     * @throws StorageException if any error occurs
     */
    protected void removeEntry(String key, boolean writePrivilegeIsExpected)
            throws StorageException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            if (!isServiceInitialized()) {
                incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, ERROR);
                LOGGER.error("Remove request while the data acess service was uninitialized.");
                throw new UninitializedServiceException();
            }

            switch (serviceRecentStatus) {
                case STARTED:
                    try {
                        if (writePrivilegeIsExpected) {
                            checkIfServiceHasWritePrivilegeFor(key);
                        } else {
                            checkIfServiceHasReadPrivilegeFor(key);
                        }
                    } catch (KeyIsNotManagedByServiceException ex) {
                        incrementCounter(KVSTORE_MODULE_NAME, REMOVE_COMMAND_NAME, NOT_RESPONSIBLE);
                        throw ex;
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
    }

    /**
     * @throws KeyIsNotManagedByServiceException if for the referred key's hash value the service
     *         does not have a WRITE privilege
     */
    private void checkIfServiceHasWritePrivilegeFor(String key)
            throws KeyIsNotManagedByServiceException {
        if (key == null || writeRange == null || !writeRange.contains(HashingUtils.getHash(key))) {
            LOGGER.error(StringUtils.join("", "Service does not have WRITE privilege for key (",
                    key, ")."));
            throw new KeyIsNotManagedByServiceException();
        }
    }

    /**
     * @throws KeyIsNotManagedByServiceException if for the referred key's hash value the service
     *         does not have a READ privilege
     */
    private void checkIfServiceHasReadPrivilegeFor(String key)
            throws KeyIsNotManagedByServiceException {
        if (key != null && readRanges != null) {
            for (HashRange range : readRanges) {
                if (range.contains(HashingUtils.getHash(key))) {
                    return;
                }
            }
        }
        LOGGER.error(
                StringUtils.join("", "Service does not have READ privilege for key (", key, ")."));
        throw new KeyIsNotManagedByServiceException();
    }

    /**
     * Builder pattern for creating a {@link MovableDataAccessService} instance.
     *
     * @author Benedek
     */
    public static class Builder<E extends Builder<E>> {
        private KVCache cache;
        private MovablePersistentStorage persistentStorage;
        private SimulatedMovableDataAccessService simulatedDataAccessService;

        @SuppressWarnings("unchecked")
        public E cache(KVCache cache) {
            this.cache = cache;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E persistentStorage(MovablePersistentStorage persistentStorage) {
            this.persistentStorage = persistentStorage;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E simulatedDataAccessService(
                SimulatedMovableDataAccessService simulatedDataAccessService) {
            this.simulatedDataAccessService = simulatedDataAccessService;
            return (E) this;
        }

        public MovableDataAccessService<E> build() {
            return new MovableDataAccessService<>(this);
        }
    }

}
