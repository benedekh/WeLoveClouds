package weloveclouds.server.services;

import static weloveclouds.server.services.models.DataAccessServiceStatus.STOPPED;

import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.RingMetadataSerializer;
import weloveclouds.server.services.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.exceptions.ServiceIsInitializedException;
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

public class MovableDataAccessService extends DataAccessService
        implements IMovableDataAccessService {

    private MovablePersistentStorage movablePersistentStorage;

    private DataAccessServiceStatus servicePreviousStatus;
    private volatile DataAccessServiceStatus serviceRecentStatus;

    private volatile RingMetadata ringMetadata;
    private volatile HashRange rangeManagedByServer;

    private ISerializer<String, RingMetadata> ringMetadatSerializer = new RingMetadataSerializer();

    public MovableDataAccessService() {

    }

    public MovableDataAccessService(KVCache cache, MovablePersistentStorage persistentStorage) {
        super(cache, persistentStorage);
        this.movablePersistentStorage = persistentStorage;
        this.servicePreviousStatus = STOPPED;
        this.serviceRecentStatus = STOPPED;
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        if (!isServiceInitialized()) {
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                checkIfKeyIsManagedByServer(entry.getKey());
                return super.putEntry(entry);
            case STOPPED:
                throw new ServiceIsStoppedException();
            case WRITELOCK_ACTIVE:
                throw new WriteLockIsActiveException();
            default:
                throw new StorageException("Storage service is not initialized yet.");
        }
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        if (!isServiceInitialized()) {
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
            case WRITELOCK_ACTIVE:
                checkIfKeyIsManagedByServer(key);
                return super.getValue(key);
            case STOPPED:
                throw new ServiceIsStoppedException();
            default:
                throw new StorageException("Storage service is not initialized yet.");
        }
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        if (!isServiceInitialized()) {
            throw new UninitializedServiceException();
        }

        switch (serviceRecentStatus) {
            case STARTED:
                checkIfKeyIsManagedByServer(key);
                super.removeEntry(key);
                break;
            case STOPPED:
                throw new ServiceIsStoppedException();
            case WRITELOCK_ACTIVE:
                throw new WriteLockIsActiveException();
            default:
                throw new StorageException("Storage service is not initialized yet.");
        }
    }

    @Override
    public void putEntries(MovableStorageUnits fromStorageUnits) throws StorageException {
        if (!isServiceInitialized()) {
            throw new UninitializedServiceException();
        }

        movablePersistentStorage.putEntries(fromStorageUnits);
    }

    @Override
    public MovableStorageUnits filterEntries(HashRange range) throws UninitializedServiceException {
        if (!isServiceInitialized()) {
            throw new UninitializedServiceException();
        }

        return movablePersistentStorage.filterEntries(range);
    }

    @Override
    public void removeEntries(HashRange range) throws StorageException {
        if (!isServiceInitialized()) {
            throw new UninitializedServiceException();
        }

        movablePersistentStorage.removeEntries(range);
    }

    @Override
    public void defragment() throws UninitializedServiceException {
        if (!isServiceInitialized()) {
            throw new UninitializedServiceException();
        }

        movablePersistentStorage.defragment();
    }

    @Override
    public void setServiceStatus(DataAccessServiceStatus serviceNewStatus)
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
    public void setRingMetadata(RingMetadata ringMetadata) {
        this.ringMetadata = ringMetadata;
    }

    @Override
    public void setManagedHashRange(HashRange rangeManagedByServer) {
        this.rangeManagedByServer = rangeManagedByServer;
    }

    @Override
    public boolean isServiceInitialized() {
        return movablePersistentStorage != null;
    }

    @Override
    public void initializeService(KVCache cache, MovablePersistentStorage persistentStorage)
            throws ServiceIsInitializedException {
        if (isServiceInitialized()) {
            throw new ServiceIsInitializedException();
        }

        super.initialize(cache, persistentStorage);
        this.movablePersistentStorage = persistentStorage;
        this.servicePreviousStatus = STOPPED;
        this.serviceRecentStatus = STOPPED;
    }

    private void checkIfKeyIsManagedByServer(String key) throws KeyIsNotManagedByServiceException {
        if (rangeManagedByServer == null
                || !rangeManagedByServer.contains(HashingUtil.getHash(key))) {
            throw new KeyIsNotManagedByServiceException(
                    ringMetadatSerializer.serialize(ringMetadata));
        }

    }

}
