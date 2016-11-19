package weloveclouds.server.models.requests.kvecs;

import weloveclouds.kvstore.models.messages.KVAdminMessage;

/**
 * Shutdown the Server itself along with every open connection.
 *
 * @author Bendek
 */
public class ShutdownServer implements IKVECSRequest {

    @Override
    public KVAdminMessage execute() {
        System.exit(0);
        return null;
    }

}
