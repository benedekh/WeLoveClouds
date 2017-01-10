package weloveclouds.server.requests.kvserver.transaction;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.networking.models.requests.IExecutable;
import weloveclouds.commons.networking.models.requests.IValidatable;

/**
 * An executable request which produces the response {@link IKVTransactionMessage}.
 * 
 * @author Benedek
 */
public interface IKVTransactionRequest
        extends IExecutable<IKVTransactionMessage>, IValidatable<IKVTransactionRequest> {

}
