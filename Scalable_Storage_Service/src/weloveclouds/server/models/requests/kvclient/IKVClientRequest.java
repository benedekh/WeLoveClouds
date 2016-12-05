package weloveclouds.server.models.requests.kvclient;

import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.commons.networking.models.requests.IExecutable;
import weloveclouds.commons.networking.models.requests.IValidatable;

/**
 * An executable request which produces the response {@link KVMessage}.
 * 
 * @author Benoit
 */
public interface IKVClientRequest extends IExecutable<KVMessage>, IValidatable<IKVClientRequest> {

}
