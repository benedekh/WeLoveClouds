package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.networking.requests.IExecutable;
import weloveclouds.commons.networking.requests.IValidatable;

/**
 * An executable request which produces the response {@link KVAdminMessage}.
 * 
 * @author Benedek
 */
public interface IKVECSRequest extends IExecutable<KVAdminMessage>, IValidatable<IKVECSRequest> {

}
