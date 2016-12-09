package weloveclouds.commons.kvstore.models;

/**
 * Represents a request between the server and the data access layer to the persistent storage /
 * cache.
 * 
 * @author Benoit
 */
public abstract class KVRequest implements IKVRequest {
    protected KVEntry entry;

    public KVRequest(KVEntry entry) {
        this.entry = entry;
    }
}
