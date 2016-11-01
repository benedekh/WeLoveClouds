package weloveclouds.server.models.requests;

import weloveclouds.kvstore.IKVMessage;

/**
 * Created by Benoit on 2016-10-31.
 */
public class Remove implements IRequest<IKVMessage> {
    @Override
    public IKVMessage execute() {
        return null;
    }
}
