package weloveclouds.server.store.models;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.utils.HashingUtils;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Represents a {@link PersistedStorageUnit} whose entries can be copied, moved or removed.
 * 
 * @author Benedek
 */
public class MovableStorageUnit extends PersistedStorageUnit {

    private static final long serialVersionUID = -5804417133252642642L;

    private Lock accessLock;

    public MovableStorageUnit(PersistedStorageUnit other) {
        super(other.entries, other.getPath());
        this.accessLock = other.accessLock;
    }

    public MovableStorageUnit(ConcurrentHashMap<String, String> entries, Path filePath) {
        super(entries, filePath);
        this.accessLock = new ReentrantLock();
    }

    public MovableStorageUnit copyEntries(HashRange range) {
        try (CloseableLock lock = new CloseableLock(accessLock)) {
            return new MovableStorageUnit(filterEntries(range), getPath());
        }
    }

    public Set<Map.Entry<String, String>> getEntries() {
        try (CloseableLock lock = new CloseableLock(accessLock)) {
            try (LoadAndSaveSilent load = new LoadAndSaveSilent()) {
                return Collections.unmodifiableSet(new HashSet<>(entries.entrySet()));
            }
        }
    }

    /**
     * Removes those entries from the storage unit whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @return the keys of the entries which were removed
     * @throws StorageException if an error occurs
     */
    public Set<String> removeEntries(HashRange range) {
        Set<String> removable = new HashSet<>();
        try (CloseableLock lock = new CloseableLock(accessLock)) {
            try (LoadAndSave load = new LoadAndSave()) {
                removable = filterEntries(range).keySet();
                for (String key : removable) {
                    try {
                        removeEntry(key);
                    } catch (StorageException ex) {
                        getLogger().error(ex);
                    }
                }
            } catch (StorageException ex) {
                getLogger().error(ex);
            }
        }
        return removable;
    }

    /**
     * Moves entries from the storage unit referred by the parameter to this storage unit until this
     * storage unit is either full or otherUnit has no more entries.
     * 
     * @param otherUnit from which entries shall be moved
     * @return the keys of the entries which were moved from the other unit
     */
    public Set<String> moveEntriesFrom(PersistedStorageUnit otherUnit) {
        Set<String> movedKeys = new HashSet<>();
        try (CloseableLock lockThis = new CloseableLock(accessLock);
                CloseableLock lockOther = new CloseableLock(otherUnit.accessLock)) {
            try (LoadAndSave loadThis = new LoadAndSave();
                    LoadAndSave loadOther = new LoadAndSave()) {
                Iterator<String> otherKeysIterator = otherUnit.getKeys().iterator();
                while (!isFull() && otherKeysIterator.hasNext()) {
                    try {
                        String key = otherKeysIterator.next();
                        putEntry(new KVEntry(key, otherUnit.getValue(key)));
                        otherUnit.removeEntry(key);
                        movedKeys.add(key);
                    } catch (StorageException ex) {
                        getLogger().error(ex);
                    }
                }
            } catch (StorageException ex) {
                getLogger().error(ex);
            }
        }
        return movedKeys;
    }

    /**
     * Filters those entries from the storage unit whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @return the key-value pairs of those entries which satisfy the filter
     */
    private ConcurrentHashMap<String, String> filterEntries(HashRange range) {
        ConcurrentHashMap<String, String> filtered = new ConcurrentHashMap<>();
        try (CloseableLock lock = new CloseableLock(accessLock)) {
            try (LoadAndSaveSilent load = new LoadAndSaveSilent()) {
                for (String key : entries.keySet()) {
                    Hash hash = HashingUtils.getHash(key);
                    if (range.contains(hash)) {
                        filtered.put(key, entries.get(key));
                    }
                }
            }
        }
        return filtered;
    }

    /**
     * Converts the object to String.
     * 
     * @param betweenEntries separator character among the entries
     * @param insideEntry separator character inside the entries
     */
    private String toStringWithDelimiter(String betweenEntries, String insideEntry) {
        StringBuilder sb = new StringBuilder();
        try (CloseableLock lock = new CloseableLock(accessLock)) {
            try (LoadAndSaveSilent load = new LoadAndSaveSilent()) {
                for (Entry<String, String> entry : entries.entrySet()) {
                    KVEntry compact = new KVEntry(entry.getKey(), entry.getValue());
                    sb.append(compact.toStringWithDelimiter(insideEntry));
                    sb.append(betweenEntries);
                }
            }
        }
        sb.setLength(sb.length() - betweenEntries.length());
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(toStringWithDelimiter("; ", "::"));
        sb.append("]");
        return sb.toString();
    }

}
