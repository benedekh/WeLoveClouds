package weloveclouds.server.store.cache.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.kvstore.KVEntryWithNumber;
import weloveclouds.server.store.exceptions.StorageException;

public class LFUStrategy implements DisplacementStrategy {

    private Map<String, KVEntryWithNumber> keyFrequencyPairs;

    public LFUStrategy() {
        this.keyFrequencyPairs = new HashMap<>();
    }

    @Override
    public KVEntry displaceEntry() throws StorageException {
        // order by frequency, the least frequent will be the first
        SortedSet<KVEntryWithNumber> sorted = new TreeSet<>(keyFrequencyPairs.values());
        KVEntryWithNumber first = sorted.first();
        return first.getEntry();
    }

    @Override
    public void putEntry(KVEntry entry) throws StorageException {
        try {
            String key = entry.getKey();
            KVEntryWithNumber initialEntry = new KVEntryWithNumber(entry, 0);
            keyFrequencyPairs.put(key, initialEntry);
        } catch (NullPointerException ex) {
            throw new StorageException("Entry cannot be null.");
        }
    }

    @Override
    public String getValue(String key) throws StorageException {
        try {
            KVEntryWithNumber record = keyFrequencyPairs.get(key);
            record.increaseNumber();
            return record.getEntry().getValue();
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try {
            keyFrequencyPairs.remove(key);
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }



}
