package weloveclouds.server.models.requests.kvclient;

import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.server.core.requests.IExecutable;

/**
 * An executable request which produces the response {@link KVMessage}.
 * 
 * @author Benoit
 */
public interface IKVClientRequest extends IExecutable<KVMessage> {
    /**
     * Executing the request it will result in a reponse {@link KVMessage}.
     */
    KVMessage execute();
}
