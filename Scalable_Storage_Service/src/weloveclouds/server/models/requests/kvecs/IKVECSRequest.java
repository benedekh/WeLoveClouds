package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.KVAdminMessage;

/**
 * An executable request which produces the response {@link KVAdminMessage}.
 * 
 * @author Benedek
 */
public interface IKVECSRequest {
    /**
     * Executing the request it will result in a reponse {@link KVAdminMessage}.
     */
    KVAdminMessage execute();
}
