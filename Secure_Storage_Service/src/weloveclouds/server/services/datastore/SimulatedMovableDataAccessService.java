package weloveclouds.server.services.datastore;

import static weloveclouds.server.monitoring.KVServerMonitoringMetricUtils.incrementCounter;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.ERROR;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.KVSTORE_MODULE_NAME;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.NOT_RESPONSIBLE;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.PUT_COMMAND_NAME;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.server.requests.kvserver.transaction.CommitReadyRequest;
import weloveclouds.server.services.datastore.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.datastore.exceptions.ServiceIsStoppedException;
import weloveclouds.server.services.datastore.exceptions.UninitializedServiceException;
import weloveclouds.server.services.datastore.exceptions.WriteLockIsActiveException;
import weloveclouds.server.services.datastore.models.DataAccessServiceStatus;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.PutType;

/**
 * A {@link IMovableDataAccessService} whose operations only simulate their behavior, but no entry
 * is stored ever. Used by {@link CommitReadyRequest} to simulate a transaction's effect on the data
 * access service.
 * 
 * @author Benedek
 */
public class SimulatedMovableDataAccessService implements IMovableDataAccessService {

    private volatile DataAccessServiceStatus servicePreviousStatus;
    private volatile DataAccessServiceStatus serviceRecentStatus;

    private Set<HashRange> readRanges;
    private volatile HashRange writeRange;
    private volatile RingMetadata ringMetadata;

    private ReentrantReadWriteLock configurationChangeLock;

    public SimulatedMovableDataAccessService() {
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
            // left empty on purpose
        }
    }

    @Override
    public void setServiceStatus(DataAccessServiceStatus serviceNewStatus)
            throws UninitializedServiceException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.writeLock())) {
            if (!isServiceInitialized()) {
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
        return this;
    }

    private PutType putEntry(KVEntry entry, boolean writePrivilegeIsExpected)
            throws StorageException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            if (!isServiceInitialized()) {
                throw new UninitializedServiceException();
            }

            switch (serviceRecentStatus) {
                case STARTED:
                    try {
                        if (writePrivilegeIsExpected) {
                            checkIfServiceHasWritePrivilegeFor(entry.getKey());
                        } else {
                            checkIfServiceHasReadPrivilegeFor(entry.getKey());
                        }
                    } catch (KeyIsNotManagedByServiceException ex) {
                        incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, NOT_RESPONSIBLE);
                        throw ex;
                    }
                    return null;
                case STOPPED:
                    throw new ServiceIsStoppedException();
                case WRITELOCK_ACTIVE:
                    throw new WriteLockIsActiveException();
                default:
                    incrementCounter(KVSTORE_MODULE_NAME, PUT_COMMAND_NAME, ERROR);
                    throw new StorageException("Unrecognized service status.");
            }
        }
    }

    private void removeEntry(String key, boolean coordinatorRoleIsExpected)
            throws StorageException {
        try (CloseableLock lock = new CloseableLock(configurationChangeLock.readLock())) {
            if (!isServiceInitialized()) {
                throw new UninitializedServiceException();
            }

            switch (serviceRecentStatus) {
                case STARTED:
                    try {
                        if (coordinatorRoleIsExpected) {
                            checkIfServiceHasWritePrivilegeFor(key);
                        } else {
                            checkIfServiceHasReadPrivilegeFor(key);
                        }
                    } catch (KeyIsNotManagedByServiceException ex) {
                        throw ex;
                    }
                    break;
                case STOPPED:
                    throw new ServiceIsStoppedException();
                case WRITELOCK_ACTIVE:
                    throw new WriteLockIsActiveException();
                default:
                    throw new StorageException("Unrecognized service status.");
            }
        }
    }

    private void checkIfServiceHasWritePrivilegeFor(String key)
            throws KeyIsNotManagedByServiceException {
        if (key == null || writeRange == null || !writeRange.contains(HashingUtils.getHash(key))) {
            throw new KeyIsNotManagedByServiceException();
        }
    }

    private void checkIfServiceHasReadPrivilegeFor(String key)
            throws KeyIsNotManagedByServiceException {
        if (key != null && readRanges != null) {
            for (HashRange range : readRanges) {
                if (range.contains(HashingUtils.getHash(key))) {
                    return;
                }
            }
        }
        throw new KeyIsNotManagedByServiceException();
    }

    @Override
    public void removeEntries(HashRange range) throws StorageException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defragment() throws UninitializedServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<MovableStorageUnit> filterEntries(HashRange range)
            throws UninitializedServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getValue(String key) throws StorageException, ValueNotFoundException {
        throw new UnsupportedOperationException();
    }

}
