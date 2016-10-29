package weloveclouds.server.store.cache.strategy;

import weloveclouds.server.store.cache.KVCache;

public interface DisplacementStrategy {

    public void displaceEntryFromCache(KVCache cache);

}
