package weloveclouds.server.requests.kvclient;

import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.networking.models.requests.IExecutable;
import weloveclouds.commons.networking.models.requests.IValidatable;

/**
 * An executable request which produces the response {@link IKVMessage}.
 * 
 * @author Benoit
 */
public interface IKVClientRequest extends IExecutable<IKVMessage>, IValidatable<IKVClientRequest> {

}
