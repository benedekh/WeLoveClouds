package weloveclouds.server.store.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.models.KVEntry;

public class MovableStorageUnit extends PersistentStorageUnit {

    private static final long serialVersionUID = -5804417133252642642L;

    public MovableStorageUnit(PersistentStorageUnit other) {
        super(other.entries, other.maxSize);
    }

    protected MovableStorageUnit(Map<String, String> entries, int maxSize) {
        super(entries, maxSize);
    }

    public MovableStorageUnit copyEntries(HashRange range) {
        return new MovableStorageUnit(filterEntries(range), maxSize);
    }

    public Set<String> removeEntries(HashRange range) {
        Set<String> removable = filterEntries(range).keySet();
        for (String key : removable) {
            entries.remove(key);
        }
        return removable;
    }

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

    public Set<String> moveEntries(PersistentStorageUnit fromOther) {
        Set<String> movedKeys = new HashSet<>();
        Iterator<String> otherKeysIterator = fromOther.getKeys().iterator();

        while (!isFull() && otherKeysIterator.hasNext()) {
            String key = otherKeysIterator.next();
            putEntry(new KVEntry(key, fromOther.getValue(key)));
            fromOther.removeEntry(key);
            movedKeys.add(key);
        }

        return movedKeys;
    }
}

