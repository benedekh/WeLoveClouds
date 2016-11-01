package weloveclouds.kvstore.models;

/**
 * Created by Benoit on 2016-10-29.
 */
public abstract class KVRequest implements IKVRequest {
    protected KVEntry entry;

    public KVRequest(KVEntry entry) {
        this.entry = entry;
    }
}
