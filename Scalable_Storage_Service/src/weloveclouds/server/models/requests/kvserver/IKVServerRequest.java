package weloveclouds.server.models.requests.kvserver;

import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.networking.requests.IExecutable;
import weloveclouds.commons.networking.requests.IValidatable;

/**
 * An executable request which produces the response {@link KVTransferMessage}.
 * 
 * @author Benedek
 */
public interface IKVServerRequest
        extends IExecutable<KVTransferMessage>, IValidatable<IKVServerRequest> {

}
