package weloveclouds.server.services.datastore;

import static weloveclouds.server.monitoring.KVServerMonitoringMetricUtils.incrementCounter;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.ERROR;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.KVSTORE_MODULE_NAME;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.NOT_RESPONSIBLE;
import static weloveclouds.server.monitoring.MonitoringMetricConstants.PUT_COMMAND_NAME;

import java.util.HashSet;
import java.util.Set;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.server.services.datastore.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.datastore.exceptions.ServiceIsStoppedException;
import weloveclouds.server.services.datastore.exceptions.UninitializedServiceException;
import weloveclouds.server.services.datastore.exceptions.WriteLockIsActiveException;
import weloveclouds.server.services.datastore.models.DataAccessServiceStatus;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.PutType;

public class SimulatedMovableDataAccessService implements IMovableDataAccessService {

    private DataAccessServiceStatus servicePreviousStatus;
    private volatile DataAccessServiceStatus serviceRecentStatus;

    private volatile RingMetadata ringMetadata;
    private Set<HashRange> readRanges;
    private volatile HashRange writeRange;

    public SimulatedMovableDataAccessService() {
        this.servicePreviousStatus = DataAccessServiceStatus.STOPPED;
        this.serviceRecentStatus = DataAccessServiceStatus.STOPPED;
        this.readRanges = new HashSet<>();
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        return putEntry(entry, true);
    }

    @Override
    public synchronized PutType putEntryWithoutAuthorization(KVEntry entry) throws StorageException {
        return putEntry(entry, false);
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
    public synchronized void putEntries(Set<MovableStorageUnit> fromStorageUnits) throws StorageException {
        // left empty on purpose
    }

    @Override
    public synchronized void setServiceStatus(DataAccessServiceStatus serviceNewStatus)
            throws UninitializedServiceException {
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

    @Override
    public synchronized void setRingMetadata(RingMetadata ringMetadata) {
        this.ringMetadata = ringMetadata;
    }

    @Override
    public synchronized RingMetadata getRingMetadata() {
        return ringMetadata;
    }

    @Override
    public synchronized void setManagedHashRanges(Set<HashRange> readRanges, HashRange writeRange) {
        if (readRanges != null) {
            this.readRanges.clear();
            this.readRanges.addAll(readRanges);
        }
        this.writeRange = writeRange;
    }

    @Override
    public synchronized boolean isServiceInitialized() {
        return ringMetadata != null && readRanges != null;
    }

    @Override
    public synchronized SimulatedMovableDataAccessService getSimulatedDataAccessService() {
        return this;
    }

    private PutType putEntry(KVEntry entry, boolean coordinatorRoleIsExpected)
            throws StorageException {
        if (!isServiceInitialized()) {
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                try {
                    if (coordinatorRoleIsExpected) {
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

    private void removeEntry(String key, boolean coordinatorRoleIsExpected)
            throws StorageException {
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
