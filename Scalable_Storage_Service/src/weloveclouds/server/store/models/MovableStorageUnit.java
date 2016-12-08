package weloveclouds.server.store.models;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Represents a {@link PersistedStorageUnit} whose entries can be copied, moved or removed.
 * 
 * @author Benedek
 */
public class MovableStorageUnit extends PersistedStorageUnit {

    private static final Logger LOGGER = Logger.getLogger(MovableStorageUnit.class);

    public MovableStorageUnit(PersistedStorageUnit other) {
        super(other.entries, other.filePath);
    }

    public MovableStorageUnit(HashMap<String, String> entries, Path filePath) {
        super(entries, filePath);
    }

    public MovableStorageUnit copyEntries(HashRange range) {
        return new MovableStorageUnit(filterEntries(range), filePath);
    }

    /**
     * Removes those entries from the storage unit whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @return the keys of the entries which were removed
     * @throws StorageException if an error occurs
     */
    public Set<String> removeEntries(HashRange range) {
        try {
            acquireLock();
            Set<String> removable = filterEntries(range).keySet();
            for (String key : removable) {
                try {
                    removeEntry(key);
                } catch (StorageException ex) {
                    LOGGER.error(ex);
                }
            }
            return removable;
        } finally {
            releaseLock();
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
        try {
            acquireLock();
            otherUnit.acquireLock();

            Set<String> movedKeys = new HashSet<>();
            Iterator<String> otherKeysIterator = otherUnit.getKeys().iterator();

            while (!isFull() && otherKeysIterator.hasNext()) {
                try {
                    String key = otherKeysIterator.next();
                    putEntry(new KVEntry(key, otherUnit.getValue(key)));
                    otherUnit.removeEntry(key);
                    movedKeys.add(key);
                } catch (StorageException ex) {
                    LOGGER.error(ex);
                }
            }

            return movedKeys;
        } finally {
            releaseLock();
            otherUnit.releaseLock();
        }
    }

    /**
     * Filters those entries from the storage unit whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @return the key-value pairs of those entries which satisfy the filter
     */
    private HashMap<String, String> filterEntries(HashRange range) {
        try {
            acquireAndLoad();
            HashMap<String, String> filtered = new HashMap<>();
            for (String key : entries.keySet()) {
                Hash hash = HashingUtil.getHash(key);
                if (range.contains(hash)) {
                    filtered.put(key, entries.get(key));
                }
            }
            return filtered;
        } finally {
            releaseAndSaveSilent();
        }
    }

    public String toStringWithDelimiter(String betweenEntries, String insideEntry) {
        try {
            acquireAndLoad();
            StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : entries.entrySet()) {
                KVEntry compact = new KVEntry(entry.getKey(), entry.getValue());
                sb.append(compact.toStringWithDelimiter(insideEntry));
                sb.append(betweenEntries);
            }
            sb.setLength(sb.length() - betweenEntries.length());
            return sb.toString();
        } finally {
            releaseAndSaveSilent();
        }
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

