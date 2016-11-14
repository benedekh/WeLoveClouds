package weloveclouds.server.store.models;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;

public class MovableStorageUnit extends PersistedStorageUnit {

    private static final long serialVersionUID = -5804417133252642642L;

    public MovableStorageUnit(PersistedStorageUnit other) {
        super(other.entries, other.getPath());
    }

    protected MovableStorageUnit(Map<String, String> entries, Path filePath) {
        super(entries, filePath);
    }

    public MovableStorageUnit copyEntries(HashRange range) {
        return new MovableStorageUnit(filterEntries(range), getPath());
    }

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
}

