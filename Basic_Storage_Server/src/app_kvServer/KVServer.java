package app_kvServer;

import weloveclouds.server.store.cache.strategy.DisplacementStrategy;
import weloveclouds.server.store.cache.strategy.FIFOStrategy;
import weloveclouds.server.store.cache.strategy.LFUStrategy;
import weloveclouds.server.store.cache.strategy.LRUStrategy;

public class KVServer {

    /**
     * Start KV Server at given port
     * 
     * @param port given port for storage server to operate
     * @param cacheSize specifies how many key-value pairs the server is allowed to keep in-memory
     * @param strategy specifies the cache replacement strategy in case the cache is full and there
     *        is a GET- or PUT-request on a key that is currently not contained in the cache.
     *        Options are "FIFO", "LRU", and "LFU".
     */
    public KVServer(int port, int cacheSize, String strategy) {
        DisplacementStrategy displacementStrategy = null;

        switch (strategy) {
            case "FIFO":
                displacementStrategy = new FIFOStrategy();
                break;
            case "LRU":
                displacementStrategy = new LRUStrategy();
                break;
            case "LFU":
                displacementStrategy = new LFUStrategy();
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid strategy. Valid values are: FIFO, LRU, LFU");
        }
    }
}
