package weloveclouds.loadbalancer.models.cache.strategies;

import com.google.inject.Inject;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import weloveclouds.loadbalancer.configuration.annotations.PopularityBasedDisplacementCapacity;
import weloveclouds.loadbalancer.configuration.annotations.PopularityBasedDisplacementRunInterval;
import weloveclouds.loadbalancer.models.cache.CachedKeyUsage;

/**
 * Created by Benoit on 2016-12-06.
 */
public class PopularityBasedDisplacementStrategy<K> implements IDisplacementStrategy<K> {
    private static final int FIRST = 0;
    private final List<CachedKeyUsage<K>> sortedCachedKeyUsage;
    private final Map<K, CachedKeyUsage<K>> keyUsage;
    private ReentrantReadWriteLock reentrantReadWriteLock;
    private ScheduledExecutorService keyOrderingTask;

    @Inject
    public PopularityBasedDisplacementStrategy(@PopularityBasedDisplacementCapacity
                                                       int maximumCapacity,
                                               @PopularityBasedDisplacementRunInterval
                                                       Duration timeBetweenKeyOrdering) {
        this.reentrantReadWriteLock = new ReentrantReadWriteLock();
        this.keyOrderingTask = Executors.newScheduledThreadPool(1);
        this.keyUsage = new LinkedHashMap<>(maximumCapacity);
        this.sortedCachedKeyUsage = new ArrayList<>();
        this.keyOrderingTask.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    reentrantReadWriteLock.writeLock().lock();
                    Collections.sort(sortedCachedKeyUsage, new Comparator<CachedKeyUsage<K>>() {
                        @Override
                        public int compare(CachedKeyUsage<K> o1, CachedKeyUsage<K> o2) {
                            return o1.getNumberOfOperationPerformedOnKey().compareTo(o2.getNumberOfOperationPerformedOnKey());
                        }
                    });
                } finally {
                    reentrantReadWriteLock.writeLock().unlock();
                }
            }
        }, 0, timeBetweenKeyOrdering.getStandardSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public K getKeyToDisplace() {
        K leastUsedKey;
        try {
            reentrantReadWriteLock.writeLock().lock();
            leastUsedKey = sortedCachedKeyUsage.remove(FIRST).getKey();
            keyUsage.remove(leastUsedKey);
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
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
            try {
                reentrantReadWriteLock.writeLock().lock();
                sortedCachedKeyUsage.add(cachedKeyUsage);
            } finally {
                reentrantReadWriteLock.writeLock().unlock();
            }
        }
    }

    @Override
    public void registerPut(K key) {
        registerGet(key);
    }
}
