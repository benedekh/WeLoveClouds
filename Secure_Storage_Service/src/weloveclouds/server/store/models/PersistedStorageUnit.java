package weloveclouds.server.store.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Representation of the data structure where the key-value pairs are persisted on the storage.
 * 
 * @author Benedek
 */
public class PersistedStorageUnit implements Serializable {

    private static final long serialVersionUID = 1582338246635272891L;
    protected static final int MAX_NUMBER_OF_ENTRIES = 100;

    protected Map<String, String> entries;
    protected volatile String filePath;

    protected ReentrantReadWriteLock accessLock;

    /**
     * @param maxSize at most how many entries can be stored in the storage unit
     */
    public PersistedStorageUnit(Path filePath) {
        setPath(filePath);
        this.entries = new ConcurrentHashMap<>();
        this.accessLock = new ReentrantReadWriteLock();
    }

    /**
     * @param initializerMap contains the key-value pairs which shall initialize this storage unit
     * @param filePath where the storage unit shall be persisted.
     */
    protected PersistedStorageUnit(Map<String, String> initializerMap, Path filePath) {
        setPath(filePath);
        this.entries = initializerMap;
        this.accessLock = new ReentrantReadWriteLock();
    }

    /**
     * @return true if the storage unit is empty, false otherwise
     */
    public boolean isEmpty() {
        try (CloseableLock lock = new CloseableLock(readLock())) {
            return entries.isEmpty();
        }
    }

    /**
     * @return true if the storage unit is full, false otherwise
     */
    public boolean isFull() {
        try (CloseableLock lock = new CloseableLock(readLock())) {
            return !(entries.size() < MAX_NUMBER_OF_ENTRIES);
        }
    }

    /**
     * @return keys stored in the storage unit as an unmodifiable set
     */
    public Set<String> getKeys() {
        try (CloseableLock lock = new CloseableLock(readLock())) {
            return Collections.unmodifiableSet(new HashSet<>(entries.keySet()));
        }
    }

    /**
     * @return path where the storage unit is stored
     */
    public Path getPath() {
        return Paths.get(filePath);
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
        try (CloseableLock lock = new CloseableLock(writeLock())) {
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
            save();

            return responseType;
        }
    }

    /**
     * @return the value which belong to the respective key
     */
    public String getValue(String key) {
        try (CloseableLock lock = new CloseableLock(readLock())) {
            return entries.get(key);
        }
    }

    /**
     * Removes the key together with the value belonging to it, from the storage unit.
     */
    public void removeEntry(String key) throws StorageException {
        try (CloseableLock lock = new CloseableLock(writeLock())) {
            entries.remove(key);
            save();
        }
    }

    /**
     * Deletes the underlying file where the data structure was serialized.
     * 
     * @throws IOException if any error occurs
     */
    public void deleteFile() throws IOException {
        try (CloseableLock lock = new CloseableLock(writeLock())) {
            PathUtils.deleteFile(getPath());
        }
    }

    /**
     * Saves this storage unit into the file denoted by its path.
     * 
     * @throws StorageException if any error occurs
     */
    public void save() throws StorageException {
        try (CloseableLock lock = new CloseableLock(writeLock())) {
            try {
                PathUtils.saveToFile(getPath(), this);
            } catch (FileNotFoundException e) {
                getLogger().error(e);
                throw new StorageException("File was not found.");
            } catch (IOException e) {
                getLogger().error(e);
                throw new StorageException(
                        "Storage unit was not saved to the persistent storage due to IO error.");
            }
        }
    }

    /**
     * Set the path where this storage unit is persisted.
     */
    public void setPath(Path path) {
        this.filePath = path.toAbsolutePath().toString();
    }

    protected Lock readLock() {
        return accessLock.readLock();
    }

    protected Lock writeLock() {
        return accessLock.writeLock();
    }

    /**
     * Because logger is not serializable, we have to get it every time.
     */
    protected Logger getLogger() {
        return Logger.getLogger(getClass());
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
