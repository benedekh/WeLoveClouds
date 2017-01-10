package weloveclouds.server.requests.kvecs;

import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.networking.models.requests.IExecutable;
import weloveclouds.commons.networking.models.requests.IValidatable;

/**
 * An executable request which produces the response {@link IKVAdminMessage}.
 * 
 * @author Benedek
 */
public interface IKVECSRequest extends IExecutable<IKVAdminMessage>, IValidatable<IKVECSRequest> {

}
