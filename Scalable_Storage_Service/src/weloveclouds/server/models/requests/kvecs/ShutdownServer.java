package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.KVAdminMessage;

public class ShutdownServer implements IKVECSRequest {

    @Override
    public KVAdminMessage execute() {
        System.exit(0);
        return null;
    }

}
