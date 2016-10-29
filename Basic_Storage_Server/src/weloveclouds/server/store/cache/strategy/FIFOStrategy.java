package weloveclouds.server.store.cache.strategy;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;

public class FIFOStrategy implements DisplacementStrategy {

    private Queue<KVEntry> fifo;

    public FIFOStrategy() {
        this.fifo = new ArrayDeque<>();
    }

    @Override
    public KVEntry displaceEntry() throws StorageException {
        try {
            return fifo.remove();
        } catch (NoSuchElementException ex) {
            throw new StorageException("FIFO is empty so it cannot remove anything.");
        }
    }

    @Override
    public void putEntry(KVEntry entry) throws StorageException {
        try {
            fifo.add(entry);
        } catch (NullPointerException ex) {
            throw new StorageException("Entry cannot be null.");
        }
    }

    @Override
    public String getValue(String key) throws StorageException {
        // FIFO strategy does not update anything
        return "";
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try {
            fifo.remove(key);
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        }
    }

}
