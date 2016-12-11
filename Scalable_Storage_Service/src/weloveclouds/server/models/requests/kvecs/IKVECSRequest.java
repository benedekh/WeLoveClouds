package weloveclouds.server.models.requests.kvecs;

import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.networking.models.requests.IExecutable;
import weloveclouds.commons.networking.models.requests.IValidatable;

/**
 * An executable request which produces the response {@link KVAdminMessage}.
 * 
 * @author Benedek
 */
public interface IKVECSRequest extends IExecutable<KVAdminMessage>, IValidatable<IKVECSRequest> {

}
