package weloveclouds.server.store;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;

/**
 * A key-value cache for the {@link DataAccessService}. It hides the {@link DisplacementStrategy}
 * which tells which key to be removed from the cache, if the cache is full.
 * 
 * @author Benedek
 */
public class KVCache implements IDataAccessService, Observer {

    private Map<String, String> cache;
    private int currentSize;
    private int capacity;

    private DisplacementStrategy displacementStrategy;
    private Logger logger;

    /**
     * @param maxSize how many entries can be stored in the cache
     * @param displacementStrategy strategy which decides which key and value to be removed from the
     *        cache, if it is full
     */
    public KVCache(int maxSize, DisplacementStrategy displacementStrategy) {
        this.capacity = maxSize;
        this.displacementStrategy = displacementStrategy;

        this.cache = new HashMap<>();
        this.currentSize = 0;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        PutType response;
        try {
            String key = entry.getKey();
            String value = entry.getValue();

            if (cache.containsKey(key)) {
                cache.put(key, value);
                response = PutType.UPDATE;
            } else {
                if (currentSize == capacity) {
                    String displacedKey = displacementStrategy.displaceKey();
                    removeEntry(displacedKey);
                }

                cache.put(key, value);
                currentSize++;
                response = PutType.INSERT;
            }

            logger.debug(CustomStringJoiner.join(" ", entry.toString(), "was added to cache."));
            displacementStrategy.put(key);

            return response;
        } catch (NullPointerException ex) {
            String errorMessage = "Key or value is null when adding element to cache.";
            logger.error(errorMessage);
            throw new StorageException(errorMessage);
        } catch (IllegalArgumentException ex) {
            logger.error(ex);
            throw new StorageException(
                    "Some property of the key or value prevents it from being stored in the cache.");
        }
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        try {
            String value = cache.get(key);
            if (value == null) {
                throw new ValueNotFoundException(key);
            } else {
                displacementStrategy.get(key);
                logger.debug(CustomStringJoiner.join(" ", value, "was retrieved from cache for key",
                        key));
                return value;
            }
        } catch (NullPointerException ex) {
            String errorMessage = "Key cannot be null to get value from cache.";
            logger.error(errorMessage);
            throw new StorageException(errorMessage);
        }
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        try {
            String removed = cache.remove(key);
            if (removed != null) {
                currentSize--;
                displacementStrategy.remove(key);
                logger.debug(CustomStringJoiner.join(" ", key, "was removed from cache."));
            }
        } catch (NullPointerException ex) {
            String errorMessage = "Key cannot be null to remove from cache.";
            logger.error(errorMessage);
            throw new StorageException(errorMessage);
        }
    }

    @Override
    public synchronized void update(Observable target, Object value) {
        try {
            if (value instanceof KVEntry) {
                putEntry((KVEntry) value);
            } else if (value instanceof String) {
                removeEntry((String) value);
            }
        } catch (StorageException ex) {
            logger.error(ex);
        }
    }

}
