package weloveclouds.server.models.requests;

import weloveclouds.commons.kvstore.models.KVMessage;

/**
 * An executable request which produces the response {@link KVMessage}.
 * 
 * @author Benoit
 */
public interface IRequest {
    /**
     * Executing the request it will result in a reponse {@link KVMessage}.
     */
    KVMessage execute();
}
