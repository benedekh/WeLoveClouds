package weloveclouds.server.requests.kvserver;

import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.networking.models.requests.IExecutable;
import weloveclouds.commons.networking.models.requests.IValidatable;

/**
 * An executable request which produces the response {@link KVTransferMessage}.
 * 
 * @author Benedek
 */
public interface IKVServerRequest
        extends IExecutable<KVTransferMessage>, IValidatable<IKVServerRequest> {

}
