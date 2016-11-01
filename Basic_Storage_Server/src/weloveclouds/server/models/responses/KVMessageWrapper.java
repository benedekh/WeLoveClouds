package weloveclouds.server.models.responses;

import weloveclouds.kvstore.IKVMessage;

/**
 * Created by Benoit on 2016-11-01.
 */
public class KVMessageWrapper implements IResponse {
    private IKVMessage kvMessage;

    public KVMessageWrapper(IKVMessage kvMessage){
        this.kvMessage = kvMessage;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
