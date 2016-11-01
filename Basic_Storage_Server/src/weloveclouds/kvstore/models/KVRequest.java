package weloveclouds.kvstore.models;

import weloveclouds.client.models.commands.ICommand;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-29.
 */
public abstract class KVRequest implements IKVRequest {
    protected KVEntry entry;

    public KVRequest(KVEntry entry) {
        this.entry = entry;
    }
}
