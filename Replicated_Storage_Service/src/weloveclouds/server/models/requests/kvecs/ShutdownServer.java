package weloveclouds.server.models.requests.kvecs;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.networking.models.requests.ICallbackRegister;

/**
 * Shutdown the Server itself along with every open connection.
 *
 * @author Benedek
 */
public class ShutdownServer implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(ShutdownServer.class);

    private ICallbackRegister callbackRegister;

    public ShutdownServer(ICallbackRegister callbackRegister) {
        this.callbackRegister = callbackRegister;
    }

    @Override
    public KVAdminMessage execute() {
        LOGGER.info("Exiting the KVServer application.");
        callbackRegister.registerCallback(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        });
        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        return this;
    }

}
