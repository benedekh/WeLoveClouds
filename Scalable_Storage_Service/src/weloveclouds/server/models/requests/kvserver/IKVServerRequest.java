package weloveclouds.server.models.requests.kvserver;

import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.core.requests.IExecutable;

/**
 * An executable request which produces the response {@link KVTransferMessage}.
 * 
 * @author Benedek
 */
public interface IKVServerRequest extends IExecutable<KVTransferMessage> {
    /**
     * Executing the request it will result in a reponse {@link KVTransferMessage}.
     */
    KVTransferMessage execute();
}
