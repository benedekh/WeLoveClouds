package weloveclouds.server.requests.kvclient;

import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.server.core.requests.IExecutable;
import weloveclouds.server.core.requests.IValidatable;

/**
 * An executable request which produces the response {@link KVMessage}.
 * 
 * @author Benoit
 */
public interface IKVClientRequest extends IExecutable<KVMessage>, IValidatable<IKVClientRequest> {

}
