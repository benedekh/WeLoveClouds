package weloveclouds.server.requests.kvclient;

import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.networking.models.requests.IExecutable;
import weloveclouds.commons.networking.models.requests.IValidatable;

/**
 * An executable request which produces the response {@link KVMessage}.
 * 
 * @author Benoit
 */
public interface IKVClientRequest extends IExecutable<KVMessage>, IValidatable<IKVClientRequest> {

}
