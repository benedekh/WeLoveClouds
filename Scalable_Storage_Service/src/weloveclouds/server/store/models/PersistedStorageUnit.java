package weloveclouds.server.store.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
public class PersistedStorageUnit implements Serializable {

    private static final long serialVersionUID = -7369636797976489112L;
    protected static final int MAX_NUMBER_OF_ENTRIES = 100;

    protected Map<String, String> entries;

    protected Path filePath;
    private Logger logger;

    /**
     * @param maxSize at most how many entries can be stored in the storage unit
     */
    public PersistedStorageUnit(Path filePath) {
        this.entries = new ConcurrentHashMap<>();
        this.filePath = filePath;
        this.logger = Logger.getLogger(getClass());
    }

    /**
     * @param initializerMap contains the key-value pairs which shall initialize this storage unit
     * @param filePath where the storage unit shall be persisted.
     */
    public PersistedStorageUnit(Map<String, String> initializerMap, Path filePath) {
        this.entries = initializerMap;
        this.filePath = filePath;
        this.logger = Logger.getLogger(getClass());
    }

    /**
     * @return true if the storage unit is empty, false otherwise
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * @return true if the storage unit is full, false otherwise
     */
    public boolean isFull() {
        return !(entries.size() < MAX_NUMBER_OF_ENTRIES);
    }

    /**
     * @return keys stored in the storage unit
     */
    public Set<String> getKeys() {
        return entries.keySet();
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
        String key = entry.getKey();
        String value = entry.getValue();

        PutType responseType = null;

        if (!entries.containsKey(key)) {
            if (entries.size() + 1 > MAX_NUMBER_OF_ENTRIES) {
                throw new UnsupportedOperationException("Storage is full, cannot add new entry.");
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

    /**
     * @return the value which belong to the respective key
     */
    public String getValue(String key) {
        return entries.get(key);
    }

    /**
     * Removes the key together with the value belonging to it, from the storage unit.
     */
    public void removeEntry(String key) throws StorageException {
        entries.remove(key);
        save();
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
     * Saves this storage unit into the file denoted by its path.
     * 
     * @throws StorageException if any error occurs
     */
    public void save() throws StorageException {
        try {
            FileUtility.<PersistedStorageUnit>saveToFile(filePath, this);
        } catch (FileNotFoundException e) {
            logger.error(e);
            throw new StorageException("File was not found.");
        } catch (IOException e) {
            logger.error(e);
            throw new StorageException(
                    "Storage unit was not saved to the persistent storage due to IO error.");
        }
    }

    /**
     * Set the path where this storage unit is persisted.
     */
    public void setPath(Path path) {
        this.filePath = path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entries == null) ? 0 : entries.hashCode());
        result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
        result = prime * result + ((logger == null) ? 0 : logger.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PersistedStorageUnit other = (PersistedStorageUnit) obj;
        if (entries == null) {
            if (other.entries != null)
                return false;
        } else if (!entries.equals(other.entries))
            return false;
        if (filePath == null) {
            if (other.filePath != null)
                return false;
        } else if (!filePath.equals(other.filePath))
            return false;
        if (logger == null) {
            if (other.logger != null)
                return false;
        } else if (!logger.equals(other.logger))
            return false;
        return true;
    }

}
