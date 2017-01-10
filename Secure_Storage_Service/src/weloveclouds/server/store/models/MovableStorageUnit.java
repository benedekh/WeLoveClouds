package weloveclouds.server.store.models;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.utils.KeyWithHash;

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

    public MovableStorageUnit(SortedMap<KeyWithHash, String> entries, Path filePath) {
        super(entries, filePath);
    }

    public MovableStorageUnit copyEntries(HashRange range) {
        try (CloseableLock lock = new CloseableLock(accessLock.readLock())) {
            return new MovableStorageUnit(filterEntries(range), getPath());
        }
    }

    public Set<Map.Entry<KeyWithHash, String>> getEntries() {
        try (CloseableLock lock = new CloseableLock(accessLock.readLock())) {
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
    public Set<KeyWithHash> removeEntries(HashRange range) {
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            Set<KeyWithHash> removable = filterEntries(range).keySet();
            for (KeyWithHash mapKey : removable) {
                try {
                    removeEntry(mapKey.getKey());
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
    public Set<KeyWithHash> moveEntriesFrom(PersistedStorageUnit otherUnit) {
        Set<KeyWithHash> movedKeys = new HashSet<>();
        try (CloseableLock lock = new CloseableLock(accessLock.writeLock())) {
            Iterator<KeyWithHash> otherMapKeyIterator = otherUnit.getKeys().iterator();
            while (!isFull() && otherMapKeyIterator.hasNext()) {
                try {
                    KeyWithHash mapKey = otherMapKeyIterator.next();
                    String key = mapKey.getKey();
                    putEntry(new KVEntry(key, otherUnit.getValue(key)));
                    otherUnit.removeEntry(key);
                    movedKeys.add(mapKey);
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
    private SortedMap<KeyWithHash, String> filterEntries(HashRange range) {
        SortedMap<KeyWithHash, String> filtered = new TreeMap<>();
        try (CloseableLock lock = new CloseableLock(accessLock.readLock())) {
            SortedMap<KeyWithHash, String> subMap = new TreeMap<>();
            KeyWithHash maxMapKey = new KeyWithHash(Hash.MAX_VALUE);
            KeyWithHash minMapKey = new KeyWithHash(Hash.MIN_VALUE);
            if (range.isOverWrapping()) {
                subMap.putAll(entries.subMap(new KeyWithHash(range.getStart()), maxMapKey));
                if (entries.containsKey(maxMapKey)) {
                    // warning: we forget the String key which belongs to Hash.MAX_VALUE
                    subMap.put(maxMapKey, entries.get(maxMapKey));
                }
                subMap.putAll(entries.subMap(minMapKey,
                        new KeyWithHash(range.getEnd().incrementByOne())));
            } else {
                if (range.getEnd().equals(Hash.MAX_VALUE)) {
                    subMap.putAll(entries.subMap(new KeyWithHash(range.getStart()), maxMapKey));
                    if (entries.containsKey(maxMapKey)) {
                        // warning: we forget the String key which belongs to Hash.MAX_VALUE
                        subMap.put(maxMapKey, entries.get(maxMapKey));
                    }
                } else {
                    subMap.putAll(entries.subMap(new KeyWithHash(range.getStart()),
                            new KeyWithHash(range.getEnd().incrementByOne())));
                }
            }
            filtered.putAll(subMap);
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
        try (CloseableLock lock = new CloseableLock(accessLock.readLock())) {
            for (Entry<KeyWithHash, String> entry : entries.entrySet()) {
                KVEntry compact = new KVEntry(entry.getKey().getKey(), entry.getValue());
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

