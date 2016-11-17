package weloveclouds.server.store.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.store.PutType;

/**
 * Representation of the data structure where the key-value pairs are persisteted on the storage.
 * 
 * @author Benedek
 */
public class PersistentStorageUnit implements Serializable {

    private static final long serialVersionUID = -7369636797976489112L;

    private Map<String, String> entries;
    private int maxSize;

    /**
     * @param maxSize at most how many entries can be stored in the storage unit
     */
    public PersistentStorageUnit(int maxSize) {
        this.entries = new HashMap<>();
        this.maxSize = maxSize;
    }

    /**
     * @param initializerMap contains the key-value pairs which shall initialize this storage unit
     * @param maxSize at most how many entries can be stored in the storage unit
     */
    public PersistentStorageUnit(Map<String, String> initializerMap, int maxSize) {
        this.entries = new HashMap<>(initializerMap);
        this.maxSize = maxSize;
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
        return !(entries.size() < maxSize);
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
    public PutType putEntry(KVEntry entry) throws UnsupportedOperationException {
        String key = entry.getKey();
        String value = entry.getValue();

        PutType responseType = null;

        if (!entries.containsKey(key)) {
            if (entries.size() + 1 > maxSize) {
                throw new UnsupportedOperationException("Storage is full, cannot add new entry.");
            } else {
                responseType = PutType.INSERT;
            }
        } else {
            responseType = PutType.UPDATE;
        }

        entries.put(key, value);
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
    public void removeEntry(String key) {
        entries.remove(key);
    }

}
