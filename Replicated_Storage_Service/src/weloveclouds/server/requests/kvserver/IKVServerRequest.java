package weloveclouds.server.requests.kvserver;

import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.core.requests.IExecutable;
import weloveclouds.server.core.requests.IValidatable;

/**
 * An executable request which produces the response {@link KVTransferMessage}.
 * 
 * @author Benedek
 */
public interface IKVServerRequest
        extends IExecutable<KVTransferMessage>, IValidatable<IKVServerRequest> {

}
