package weloveclouds.server.store.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.store.PutType;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.utils.FileUtility;

/**
 * Representation of the data structure where the key-value pairs are persisted on the storage.
 * 
 * @author Benedek
 */
public class PersistedStorageUnit {

    protected static final int MAX_NUMBER_OF_ENTRIES = 1000;
    private static final Logger LOGGER = Logger.getLogger(PersistedStorageUnit.class);

    protected HashMap<String, String> entries;
    protected boolean canBeSavedInMemory;
    protected Path filePath;

    protected ReentrantLock accessLock;

    /**
     * @param maxSize at most how many entries can be stored in the storage unit
     */
    public PersistedStorageUnit(Path filePath) {
        this.filePath = filePath;
        this.entries = new HashMap<>();
        this.accessLock = new ReentrantLock();
        this.canBeSavedInMemory = false;
    }

    /**
     * @param initializerMap contains the key-value pairs which shall initialize this storage unit
     * @param filePath where the storage unit shall be persisted.
     */
    protected PersistedStorageUnit(HashMap<String, String> initializerMap, Path filePath) {
        this.filePath = filePath;
        this.entries = initializerMap;
        this.accessLock = new ReentrantLock();
        this.canBeSavedInMemory = false;
        saveSilent();
    }

    /**
     * Enable saving the persisted storage into memory (besides disk).
     */
    public void enableSavingIntoMemory() {
        load();
        canBeSavedInMemory = true;
    }

    /**
     * Disable saving the persisted storage into memory (besides disk).
     */
    public void disableSavingIntoMemory() {
        canBeSavedInMemory = false;
        saveSilent();
    }

    /**
     * @return true if the storage unit is empty, false otherwise
     */
    public boolean isEmpty() {
        try {
            acquireAndLoad();
            return entries.isEmpty();
        } finally {
            releaseAndSaveSilent();
        }
    }


    /**
     * @return true if the storage unit is full, false otherwise
     */
    public boolean isFull() {
        try {
            acquireAndLoad();
            return !(entries.size() < MAX_NUMBER_OF_ENTRIES);
        } finally {
            releaseAndSaveSilent();
        }
    }

    /**
     * @return keys stored in the storage unit as an unmodifiable set
     */
    public Set<String> getKeys() {
        try {
            acquireAndLoad();
            return Collections.unmodifiableSet(new HashSet<>(entries.keySet()));
        } finally {
            releaseAndSaveSilent();
        }
    }

    /**
     * Puts an entry into the storage unit.
     * 
     * @return type of the operation that was executed. Either {@link PutType#INSERT} if the entry
     *         key was not stored before, or {@link PutType#UPDATE} otherwise
     * @throws UnsupportedOperationException if the storage unit does not have enough free space to
     *         store the entry
     */
    public PutType putEntry(KVEntry entry) throws UnsupportedOperationException, StorageException {
        try {
            acquireAndLoad();

            String key = entry.getKey();
            String value = entry.getValue();

            PutType responseType = null;

            if (!entries.containsKey(key)) {
                if (entries.size() + 1 > MAX_NUMBER_OF_ENTRIES) {
                    throw new UnsupportedOperationException(
                            "Storage is full, cannot add new entry.");
                } else {
                    responseType = PutType.INSERT;
                }
            } else {
                responseType = PutType.UPDATE;
            }

            entries.put(key, value);
            return responseType;
        } finally {
            releaseAndSave();
        }
    }

    /**
     * @return the value which belong to the respective key
     */
    public String getValue(String key) {
        try {
            acquireAndLoad();
            return entries.get(key);
        } finally {
            releaseAndSaveSilent();
        }
    }

    /**
     * Removes the key together with the value belonging to it, from the storage unit.
     */
    public void removeEntry(String key) throws StorageException {
        try {
            acquireAndLoad();
            entries.remove(key);
        } finally {
            releaseAndSaveSilent();
        }
    }

    /**
     * Deletes the underlying file where the data structure was serialized.
     * 
     * @throws IOException if any error occurs
     */
    public void deleteFile() throws IOException {
        FileUtility.deleteFile(filePath);
    }

    /**
     * Saves the content of the storage unit into the file denoted by its path.
     * 
     * @throws StorageException if any error occurs
     */
    public void save() throws StorageException {
        try {
            FileUtility.saveToFile(filePath, entries);
            if (!canBeSavedInMemory) {
                entries = null;
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(e);
            throw new StorageException("File was not found.");
        } catch (IOException e) {
            LOGGER.error(e);
            throw new StorageException(
                    "Storage unit was not saved to the persistent storage due to IO error.");
        }
    }

    /**
     * Loads the entry map from the file where it is stored.
     */
    private void load() {
        try {
            if (!(canBeSavedInMemory && entries != null)) {
                entries = FileUtility.<HashMap<String, String>>loadFromFile(filePath);
            }
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.error(e);
            entries = new HashMap<>();
        }
    }

    /**
     * Set the path where this storage unit is persisted.
     */
    public void setPath(Path path) {
        this.filePath = path;
    }

    /**
     * Locks this instance to prevent others from using it.
     */
    protected void acquireLock() {
        accessLock.lock();
    }

    /**
     * Releases the lock so others can use this instance.
     */
    protected void releaseLock() {
        accessLock.unlock();
    }

    /**
     * Releases the lock and saves the entry map.
     * 
     * @throws StorageException if an error occurs
     */
    protected void releaseAndSave() throws StorageException {
        releaseLock();
        save();
    }

    /**
     * Releases the lock and saves the entry map. Discards exceptions.
     */
    protected void releaseAndSaveSilent() {
        releaseLock();
        saveSilent();
    }

    /**
     * Saves the entry map silent to the disk.
     */
    private void saveSilent() {
        try {
            save();
        } catch (StorageException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Acquires the lock and loads the entry map.
     */
    protected void acquireAndLoad() {
        acquireLock();
        load();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entries == null) ? 0 : entries.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PersistedStorageUnit)) {
            return false;
        }
        PersistedStorageUnit other = (PersistedStorageUnit) obj;
        if (entries == null) {
            if (other.entries != null) {
                return false;
            }
        } else if (!entries.equals(other.entries)) {
            return false;
        }
        return true;
    }

}
