package weloveclouds.server.store.cache.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.KeyFrequency;
import weloveclouds.server.store.exceptions.StorageException;

public class LFUStrategy implements DisplacementStrategy {

    private Map<String, KeyFrequency> keyFrequencyPairs;

    private Logger logger;

    public LFUStrategy() {
        this.keyFrequencyPairs = new HashMap<>();
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public synchronized String displaceKey() throws StorageException {
        try {
            // order by frequency, the least frequent will be the first
            SortedSet<KeyFrequency> sorted = new TreeSet<>(keyFrequencyPairs.values());
            KeyFrequency first = sorted.first();
            String key = first.getKey();
            remove(key);
            return key;
        } catch (NoSuchElementException ex) {
            throw new StorageException("Store is empty so it cannot remove anything.");
        }
    }

    @Override
    public synchronized void put(String key) {
        try {
            KeyFrequency keyFrequency = new KeyFrequency(key, 0);
            keyFrequencyPairs.put(key, keyFrequency);;
        } catch (NullPointerException ex) {
            logger.error("Key cannot be null for put.");
        }
    }

    public synchronized void get(String key) {
        try {
            KeyFrequency keyFrequency = keyFrequencyPairs.get(key);
            keyFrequency.increaseFrequency();
        } catch (NullPointerException ex) {
            logger.error("Key cannot be null for get.");
        }
    }

    @Override
    public synchronized void remove(String key) {
        try {
            keyFrequencyPairs.remove(key);
        } catch (NullPointerException ex) {
            logger.error("Key cannot be null for remove.");
        }
    }



}
