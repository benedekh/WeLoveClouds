package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.core.requests.IExecutable;

/**
 * An executable request which produces the response {@link KVAdminMessage}.
 * 
 * @author Benedek
 */
public interface IKVECSRequest extends IExecutable<KVAdminMessage> {
    /**
     * Executing the request it will result in a reponse {@link KVAdminMessage}.
     */
    KVAdminMessage execute();
}
