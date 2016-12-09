package weloveclouds.server.store.models;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.utils.HashingUtil;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * Represents a {@link PersistedStorageUnit} whose entries can be copied, moved or removed.
 * 
 * @author Benedek
 */
public class MovableStorageUnit extends PersistedStorageUnit {

    private static final long serialVersionUID = -5804417133252642642L;

    public MovableStorageUnit(PersistedStorageUnit other) {
        super(other.entries, other.getPath());
    }

    public MovableStorageUnit(Map<String, String> entries, Path filePath) {
        super(entries, filePath);
    }

    public MovableStorageUnit copyEntries(HashRange range) {
        return new MovableStorageUnit(filterEntries(range), getPath());
    }

    /**
     * Removes those entries from the storage unit whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @return the keys of the entries which were removed
     * @throws StorageException if an error occurs
     */
    public Set<String> removeEntries(HashRange range) {
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

    /**
     * Moves entries from the storage unit referred by the parameter to this storage unit until this
     * storage unit is either full or otherUnit has no more entries.
     * 
     * @param otherUnit from which entries shall be moved
     * @return the keys of the entries which were moved from the other unit
     */
    public Set<String> moveEntriesFrom(PersistedStorageUnit otherUnit) {
        Set<String> movedKeys = new HashSet<>();
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
        for (String key : entries.keySet()) {
            Hash hash = HashingUtil.getHash(key);
            if (range.contains(hash)) {
                filtered.put(key, entries.get(key));
            }
        }
        return filtered;
    }

    public String toStringWithDelimiter(String betweenEntries, String insideEntry) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : entries.entrySet()) {
            KVEntry compact = new KVEntry(entry.getKey(), entry.getValue());
            sb.append(compact.toStringWithDelimiter(insideEntry));
            sb.append(betweenEntries);
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

