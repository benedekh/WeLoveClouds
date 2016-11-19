package weloveclouds.server.services;

import static weloveclouds.server.services.models.DataAccessServiceStatus.*;

import weloveclouds.hashing.models.HashRange;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.services.models.DataAccessServiceStatus;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.MovablePersistentStorage;
import weloveclouds.server.store.PutType;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.MovableStorageUnits;

public class MovableDataAccessService extends DataAccessService
        implements IMovableDataAccessService {

    private DataAccessServiceStatus servicePreviousStatus;
    private volatile DataAccessServiceStatus serviceRecentStatus;

    protected MovablePersistentStorage movablePersistentStorage;

    public MovableDataAccessService(KVCache cache, MovablePersistentStorage persistentStorage) {
        super(cache, persistentStorage);
        this.movablePersistentStorage = persistentStorage;
        this.servicePreviousStatus = STOPPED;
        this.serviceRecentStatus = STOPPED;
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        switch (serviceRecentStatus) {
            case STARTED:
                return super.putEntry(entry);
            case STOPPED:
                throw new StorageException("Storage service is stopped.");
            case WRITELOCK_ACTIVE:
                throw new StorageException("Storage service is not writable.");
            default:
                throw new StorageException("Storage service is not initialized yet.");
        }
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        switch (serviceRecentStatus) {
            case STARTED:
            case WRITELOCK_ACTIVE:
                return super.getValue(key);
            case STOPPED:
                throw new StorageException("Storage service is stopped.");
            default:
                throw new StorageException("Storage service is not initialized yet.");
        }
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        switch (serviceRecentStatus) {
            case STARTED:
                super.removeEntry(key);
                break;
            case STOPPED:
                throw new StorageException("Storage service is stopped.");
            case WRITELOCK_ACTIVE:
                throw new StorageException("Storage service is not writable.");
            default:
                throw new StorageException("Storage service is not initialized yet.");
        }
    }

    @Override
    public void putEntries(MovableStorageUnits fromStorageUnits) throws StorageException {
        movablePersistentStorage.putEntries(fromStorageUnits);
    }

    @Override
    public MovableStorageUnits filterEntries(HashRange range) {
        return movablePersistentStorage.filterEntries(range);
    }

    @Override
    public void removeEntries(HashRange range) throws StorageException {
        movablePersistentStorage.removeEntries(range);
    }

    @Override
    public void defragment() {
        movablePersistentStorage.defragment();
    }

    @Override
    public void setServiceStatus(DataAccessServiceStatus serviceNewStatus) {
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
