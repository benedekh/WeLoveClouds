package weloveclouds.server.models.requests;

import weloveclouds.kvstore.IKVMessage;

/**
 * Created by Benoit on 2016-10-31.
 */
public class Put implements IRequest<IKVMessage> {
    @Override
    public IKVMessage execute() {
        return null;
    }
}
