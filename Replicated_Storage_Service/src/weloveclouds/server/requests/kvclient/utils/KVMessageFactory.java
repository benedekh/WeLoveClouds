package weloveclouds.server.requests.kvclient.utils;

import weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVMessage;

/**
 * A factory to create {@link KVMessage} instances.
 * 
 * @author Benedek
 */
public class KVMessageFactory {

    public static KVMessage createKVMessage(StatusType status, String key, String value) {
        return new KVMessage.Builder().status(status).key(key).value(value).build();
    }

}
