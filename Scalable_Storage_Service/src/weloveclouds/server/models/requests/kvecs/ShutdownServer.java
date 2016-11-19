package weloveclouds.server.models.requests.kvecs;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.KVAdminMessage;

/**
 * Shutdown the Server itself along with every open connection.
 *
 * @author Bendek
 */
public class ShutdownServer implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(ShutdownServer.class);

    @Override
    public KVAdminMessage execute() {
        LOGGER.info("Exiting the KVServer application.");
        System.exit(0);
        return null;
    }

}
