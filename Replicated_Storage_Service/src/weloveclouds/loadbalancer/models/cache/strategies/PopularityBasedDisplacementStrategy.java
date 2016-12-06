package weloveclouds.loadbalancer.models.cache.strategies;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import weloveclouds.loadbalancer.models.cache.CachedKeyUsage;

/**
 * Created by Benoit on 2016-12-06.
 */
public class PopularityBasedDisplacementStrategy<K> implements IDisplacementStrategy<K> {
    private static final int FIRST = 0;
    private final List<CachedKeyUsage<K>> sortedCachedKeyUsage;
    private final Map<K, CachedKeyUsage<K>> keyUsage;
    ScheduledExecutorService keyOrderingTask;

    public PopularityBasedDisplacementStrategy(int maximumCapacity, int
            timeBetweenKeyOrderingInSec) {
        keyOrderingTask = Executors.newScheduledThreadPool(10);
        keyUsage = new LinkedHashMap<>(maximumCapacity);
        sortedCachedKeyUsage = new ArrayList<>();
        keyOrderingTask.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (sortedCachedKeyUsage) {
                    Collections.sort(sortedCachedKeyUsage, new Comparator<CachedKeyUsage<K>>() {
                        @Override
                        public int compare(CachedKeyUsage<K> o1, CachedKeyUsage<K> o2) {
                            return o1.getNumberOfOperationPerformed().compareTo(o2.getNumberOfOperationPerformed());
                        }
                    });
                }
            }
        }, 0, timeBetweenKeyOrderingInSec, TimeUnit.SECONDS);
    }

    @Override
    public K getKeyToDisplace() {
        K leastUsedKey = null;
        synchronized (sortedCachedKeyUsage) {
            leastUsedKey = sortedCachedKeyUsage.remove(FIRST).getKey();
        }
        keyUsage.remove(leastUsedKey);
        return leastUsedKey;
    }

    @Override
    public void registerGet(K key) {
        CachedKeyUsage<K> cachedKeyUsage = keyUsage.get(key);
        if (cachedKeyUsage != null) {
            cachedKeyUsage.incrementNumberOfOperationPerformed();
        } else {
            cachedKeyUsage = new CachedKeyUsage<>(key);
            cachedKeyUsage.incrementNumberOfOperationPerformed();
            keyUsage.put(key, cachedKeyUsage);
            synchronized (sortedCachedKeyUsage) {
                sortedCachedKeyUsage.add(cachedKeyUsage);
            }
        }
    }

    @Override
    public void registerPut(K key) {
        registerGet(key);
    }
}
