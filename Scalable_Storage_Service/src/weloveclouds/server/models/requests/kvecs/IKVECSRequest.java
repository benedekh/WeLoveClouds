package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.core.requests.IExecutable;
import weloveclouds.server.core.requests.IValidatable;

/**
 * An executable request which produces the response {@link KVAdminMessage}.
 * 
 * @author Benedek
 */
public interface IKVECSRequest extends IExecutable<KVAdminMessage>, IValidatable<IKVECSRequest> {

}
