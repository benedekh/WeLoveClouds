package weloveclouds.server.store.models;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    private Lock readLock;
    private Lock writeLock;

    public MovableStorageUnit(PersistedStorageUnit other) {
        super(other.entries, other.getPath());
        this.readLock = other.readLock();
        this.writeLock = other.writeLock();
    }

    public MovableStorageUnit(Map<String, String> entries, Path filePath) {
        super(entries, filePath);
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    /**
     * Copies those entries whose key's hash value is in the given range.
     */
    public MovableStorageUnit copyEntries(HashRange range) {
        try (CloseableLock lock = new CloseableLock(readLock)) {
            return new MovableStorageUnit(filterEntries(range), getPath());
        }
    }

    /**
     * @return an unmodifiable view of the entries
     */
    public Set<Map.Entry<String, String>> getEntries() {
        try (CloseableLock lock = new CloseableLock(readLock)) {
            return Collections.unmodifiableSet(new HashSet<>(entries.entrySet()));
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
        try (CloseableLock lock = new CloseableLock(writeLock)) {
            Set<String> removable = filterEntries(range).keySet();
            for (String key : removable) {
                try {
                    removeEntry(key);
                } catch (StorageException ex) {
                    getLogger().error(ex);
                }
            }
            return removable;
        }
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
        try (CloseableLock lock = new CloseableLock(writeLock)) {
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
        }
        return movedKeys;
    }

    /**
     * Filters those entries from the storage unit whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @return the key-value pairs of those entries which satisfy the filter
     */
    private Map<String, String> filterEntries(HashRange range) {
        Map<String, String> filtered = new HashMap<>();
        try (CloseableLock lock = new CloseableLock(readLock)) {
            for (String key : entries.keySet()) {
                Hash hash = HashingUtils.getHash(key);
                if (range.contains(hash)) {
                    filtered.put(key, entries.get(key));
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
        try (CloseableLock lock = new CloseableLock(readLock)) {
            for (Entry<String, String> entry : entries.entrySet()) {
                KVEntry compact = new KVEntry(entry.getKey(), entry.getValue());
                sb.append(compact.toStringWithDelimiter(insideEntry));
                sb.append(betweenEntries);
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

