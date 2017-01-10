package weloveclouds.server.store;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.services.datastore.DataAccessService;
import weloveclouds.server.services.datastore.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.PersistedStorageUnit;
import weloveclouds.server.store.models.PutType;

/**
 * The persistent storage for the {@link DataAccessService}} which stores the key-value pairs in
 * their respective {@link PersistedStorageUnit}s.
 * 
 * @author Benedek
 */
public class KVPersistentStorage extends Observable implements IDataAccessService {

    protected static final String FILE_EXTENSION = "ser";
    private static final Logger LOGGER = Logger.getLogger(KVPersistentStorage.class);

    protected Map<String, PersistedStorageUnit> storageUnits;
    protected Queue<PersistedStorageUnit> unitsWithFreeSpace;

    protected Path rootPath;
    protected ReentrantReadWriteLock accessLock;

    public KVPersistentStorage(Path rootPath) throws IllegalArgumentException {
        if (rootPath == null || !rootPath.toAbsolutePath().toFile().exists()) {
            throw new IllegalArgumentException("Root path does not exist.");
        }

        this.rootPath = rootPath.toAbsolutePath();
        this.storageUnits = new ConcurrentHashMap<>();
        this.unitsWithFreeSpace = new ArrayDeque<>();
        this.accessLock = new ReentrantReadWriteLock();
    }

    @Override
    public PutType putEntry(KVEntry entry) throws StorageException {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            String key = entry.getKey();
            PutType response;

            if (key == null || entry.getValue() == null) {
                throw new StorageException("Key and value cannot be null.");
            } else if (storageUnits.containsKey(key)) {
                response = PutType.UPDATE;
                PersistedStorageUnit storageUnit = storageUnits.get(key);
                putEntryIntoStorageUnit(storageUnit, entry);
            } else {
                response = PutType.INSERT;
                // see if there is any storage unit with free spaces
                if (!unitsWithFreeSpace.isEmpty()) {
                    // if there is, append the new record to it
                    PersistedStorageUnit storageUnit = unitsWithFreeSpace.peek();
                    putEntryIntoStorageUnit(storageUnit, entry);
                } else {
                    Path path = PathUtils.generateUniqueFilePath(rootPath, FILE_EXTENSION);
                    PersistedStorageUnit storageUnit = new PersistedStorageUnit(path);
                    putEntryIntoStorageUnit(storageUnit, entry);
                }
            }

            LOGGER.debug(StringUtils.join(" ", entry, "is persisted to permanent storage unit."));
            notifyObservers(entry);

            return response;
        }
    }

    @Override
    public String getValue(String key) throws StorageException, ValueNotFoundException {
        try (CloseableLock lock = new CloseableLock(accessLock.readLock())) {
            if (!storageUnits.containsKey(key)) {
                throw new ValueNotFoundException(key);
            }

            PersistedStorageUnit storageUnit = storageUnits.get(key);
            String value = storageUnit.getValue(key);

            LOGGER.debug(StringUtils.join("", "Value <", value, "> is read for key <", key, "> ."));
            return value;
        }
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            try {
                if (storageUnits.containsKey(key)) {
                    PersistedStorageUnit storageUnit = storageUnits.get(key);
                    storageUnit.removeEntry(key);
                    if (storageUnit.isEmpty()) {
                        removeStorageUnit(storageUnit);
                    } else {
                        putStorageUnitIntoFreeSpaceCache(storageUnit);
                    }
                }
            } catch (NullPointerException ex) {
                String errorMessage = "Key cannot be null for removing from persistent storage.";
                LOGGER.error(errorMessage);
                throw new StorageException(errorMessage);
            } catch (NoSuchFileException ex) {
                storageUnits.remove(key);
                String errorMessage = StringUtils.join(" ", "File for key", key,
                        "was already removed from persistent storage.");
                LOGGER.error(errorMessage);
                throw new StorageException(errorMessage);
            } catch (IOException e) {
                LOGGER.error(e);
                throw new StorageException(
                        "File for key cannot be removed from persistent storage due to permission problems.");
            }
            removeKeyFromStore(key);
        }
    }

    @Override
    public void notifyObservers(Object object) {
        setChanged();
        super.notifyObservers(object);
    }

    /**
     * Puts the respective storage unit into the cache for free storage units, if the storage unit
     * is not full and the cache does not contain it yet.
     */
    protected void putStorageUnitIntoFreeSpaceCache(PersistedStorageUnit storageUnit) {
        if (!storageUnit.isFull() && !unitsWithFreeSpace.contains(storageUnit)) {
            unitsWithFreeSpace.add(storageUnit);
        }
    }

    /**
     * Removes the respective storage unit from the cache for free storage units.
     */
    protected void removeStorageUnitFromFreeSpaceCache(PersistedStorageUnit storageUnit) {
        unitsWithFreeSpace.remove(storageUnit);
    }

    /**
     * Removes the respective key from the store.
     */
    protected void removeKeyFromStore(String key) {
        storageUnits.remove(key);
        notifyObservers(key);
    }

    /**
     * Removes the respective storage unit from the cache for free storage units.
     */
    protected void removeStorageUnit(PersistedStorageUnit storageUnit) throws IOException {
        removeStorageUnitFromFreeSpaceCache(storageUnit);
        storageUnit.deleteFile();
    }

    /**
     * Puts the respective entry into a storage unit. In case the storage unit is full, it creates a
     * new {@link PersistedStorageUnit} and puts the entry into that.<br>
     * Besides, it puts the parameter respective storageUnit into the {@link #storageUnits} map in
     * case the entry was put into that. Updates the #unitsWithFreeSpace accordingly too.
     * 
     * @throws StorageException if any error occurs
     */
    private void putEntryIntoStorageUnit(PersistedStorageUnit storageUnit, KVEntry entry)
            throws StorageException {
        try {
            storageUnit.putEntry(entry);
            storageUnits.put(entry.getKey(), storageUnit);
            putStorageUnitIntoFreeSpaceCache(storageUnit);
        } catch (UnsupportedOperationException ex) {
            removeStorageUnitFromFreeSpaceCache(storageUnit);

            Path path = PathUtils.generateUniqueFilePath(rootPath, FILE_EXTENSION);
            PersistedStorageUnit newStorageUnit = new PersistedStorageUnit(path);

            newStorageUnit.putEntry(entry);
            storageUnits.put(entry.getKey(), newStorageUnit);
            putStorageUnitIntoFreeSpaceCache(newStorageUnit);
        }
    }

}
