package weloveclouds.server.store.storage;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.KVEntry;
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
 * their respective {@link PersistedStorageUnit}s on disk (anything denoted by
 * #{@link KVPersistentStorage#rootPath}.
 * 
 * @author Benedek
 */
public class KVPersistentStorage extends Observable implements IDataAccessService {

    protected static final String FILE_EXTENSION = "ser";
    private static final Logger LOGGER = Logger.getLogger(KVPersistentStorage.class);

    protected Map<String, PersistedStorageUnit> storageUnits;
    protected Queue<PersistedStorageUnit> unitsWithFreeSpace;

    protected Path rootPath;

    public KVPersistentStorage(Path rootPath) throws IllegalArgumentException {
        if (rootPath == null || !rootPath.toAbsolutePath().toFile().exists()) {
            throw new IllegalArgumentException("Root path does not exist.");
        }

        this.rootPath = rootPath.toAbsolutePath();
        this.storageUnits = new ConcurrentHashMap<>();
        this.unitsWithFreeSpace = new ConcurrentLinkedDeque<>();
    }

    @Override
    public PutType putEntry(KVEntry entry) throws StorageException {
        String key = entry.getKey();
        PersistedStorageUnit storageUnit = null;
        PutType response = null;

        if (key == null || entry.getValue() == null) {
            throw new StorageException("Key and value cannot be null.");
        } else if (storageUnits.containsKey(key)) {
            storageUnit = storageUnits.get(key);
        } else {
            // see if there is any storage unit with free spaces
            if (!unitsWithFreeSpace.isEmpty()) {
                // if there is, append the new record to it
                storageUnit = unitsWithFreeSpace.peek();
            } else {
                storageUnit = createNewStorageUnit();
            }
        }

        response = putEntryIntoStorageUnit(storageUnit, entry);
        LOGGER.debug(StringUtils.join(" ", entry, "is persisted to permanent storage unit."));
        notifyObservers(entry);

        return response;
    }

    @Override
    public String getValue(String key) throws StorageException, ValueNotFoundException {
        if (!storageUnits.containsKey(key)) {
            throw new ValueNotFoundException(key);
        }

        PersistedStorageUnit storageUnit = storageUnits.get(key);
        String value = storageUnit.getValue(key);

        LOGGER.debug(StringUtils.join("", "Value <", value, "> is read for key <", key, "> ."));
        return value;
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try {
            if (storageUnits.containsKey(key)) {
                PersistedStorageUnit storageUnit = storageUnits.remove(key);
                storageUnit.removeEntry(key);
                if (storageUnit.isEmpty()) {
                    synchronized (storageUnit) {
                        if (storageUnit.isEmpty()) {
                            removeStorageUnit(storageUnit);
                        }
                    }
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
    private PutType putEntryIntoStorageUnit(PersistedStorageUnit storageUnit, KVEntry entry)
            throws StorageException {
        synchronized (storageUnit) {
            PutType putType = null;
            try {
                putType = storageUnit.putEntry(entry);
                String key = entry.getKey();
                if (!storageUnits.containsKey(key)) {
                    synchronized (storageUnits) {
                        if (!storageUnits.containsKey(key)) {
                            // if it is the first time we put the key
                            storageUnits.put(key, storageUnit);
                            putStorageUnitIntoFreeSpaceCache(storageUnit);
                        } else {
                            // if the key was put concurrently to the storage
                            // just beforehand us, then update the value in that
                            // storage unit, and delete the one we created
                            PersistedStorageUnit storedStorageUnit = storageUnits.get(key);
                            synchronized (storedStorageUnit) {
                                putType = storedStorageUnit.putEntry(entry);
                                storageUnits.put(key, storedStorageUnit);
                                putStorageUnitIntoFreeSpaceCache(storedStorageUnit);
                            }
                            try {
                                storageUnit.deleteFile();
                            } catch (IOException ex) {
                                LOGGER.error(ex);
                            }
                        }
                    }
                } else {
                    // if the key was already stored
                    storageUnits.put(key, storageUnit);
                    putStorageUnitIntoFreeSpaceCache(storageUnit);
                }
            } catch (UnsupportedOperationException ex) {
                // if storageUnit was full
                removeStorageUnitFromFreeSpaceCache(storageUnit);
                PersistedStorageUnit newStorageUnit = createNewStorageUnit();
                putType = putEntryIntoStorageUnit(newStorageUnit, entry);
            }
            return putType;
        }
    }

    protected PersistedStorageUnit createNewStorageUnit() {
        Path path = PathUtils.generateUniqueFilePath(rootPath, FILE_EXTENSION);
        return new PersistedStorageUnit(path);
    }

}
