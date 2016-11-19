package weloveclouds.server.models.requests.kvserver;

import weloveclouds.kvstore.models.messages.KVTransferMessage;

/**
 * An executable request which produces the response {@link KVTransferMessage}.
 * 
 * @author Benedek
 */
public interface IKVServerRequest {
    /**
     * Executing the request it will result in a reponse {@link KVTransferMessage}.
     */
    KVTransferMessage execute();
}
