package weloveclouds.server.models.requests.kvclient;

import weloveclouds.kvstore.models.messages.KVMessage;

/**
 * An executable request which produces the response {@link KVMessage}.
 * 
 * @author Benoit
 */
public interface IKVClientRequest {
    /**
     * Executing the request it will result in a reponse {@link KVMessage}.
     */
    KVMessage execute();
}
