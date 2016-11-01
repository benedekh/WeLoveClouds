package weloveclouds.server.models.requests;

import weloveclouds.kvstore.KVMessage;

/**
 * Created by Benoit on 2016-10-31.
 */
public interface IRequest {
    KVMessage execute();
}
