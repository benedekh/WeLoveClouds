package weloveclouds.evaluation.dataloading.connection;

import static weloveclouds.evaluation.dataloading.util.StringJoinerUtility.join;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;

/**
 * The type Client connection.
 */
public class ClientConnection {

    private static final Logger LOGGER = LogManager.getLogger(ClientConnection.class);

    private IKVCommunicationApiV2 serverCommunication;
    private IDeserializer<RingMetadata, String> ringMetadataDeserializer;

    /**
     * Instantiates a new Client connection.
     *
     * @param serverCommunication      the server communication
     * @param ringMetadataDeserializer the ring metadata deserializer
     */
    public ClientConnection(IKVCommunicationApiV2 serverCommunication,
            IDeserializer<RingMetadata, String> ringMetadataDeserializer) {
        this.serverCommunication = serverCommunication;
        this.ringMetadataDeserializer = ringMetadataDeserializer;
    }

    /**
     * Connect.
     */
    public void connect() {
        try {
            serverCommunication.connect();
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Put.
     *
     * @param key                the key
     * @param value              the value
     * @param wasAlreadyExecuted the was already executed
     */
    public void put(String key, String value, boolean wasAlreadyExecuted) {
        try {
            LOGGER.info(
                    join(" ", "Sending key-value pair over the network: <", key, "::", value, ">"));
            IKVMessage response = serverCommunication.put(key, value);
            switch (response.getStatus()) {
                case PUT_SUCCESS:
                    LOGGER.info("Key was successfully put on server");
                    break;
                case PUT_ERROR:
                    LOGGER.error(join(": ", "Error during key put", response.getValue()));
                    break;
                case SERVER_NOT_RESPONSIBLE:
                    try {
                        LOGGER.error(join(" ", "Server is not responsible for the key:", key,
                                ". Updating ring metadata information."));

                        RingMetadata ringMetadata =
                                ringMetadataDeserializer.deserialize(response.getValue());
                        serverCommunication.setRingMetadata(ringMetadata);

                        if (!wasAlreadyExecuted) {
                            put(key, value, true);
                        } else {
                            LOGGER.error(
                                    "Put command execution failed, because responsible server was not found.");
                        }
                    } catch (DeserializationException e) {
                        LOGGER.error(e);
                    }
                    break;
                case SERVER_WRITE_LOCK:
                    LOGGER.error("Write lock is active on the server.");
                    break;
                case SERVER_STOPPED:
                    LOGGER.error("Server stopped.");
                    break;
                default:
                    LOGGER.error("Unexpected response type.");
                    break;
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
        } finally {
            LOGGER.info("Send finished.");
        }
    }

    /**
     * Close connection.
     */
    public void closeConnection() {
        serverCommunication.disconnect();
    }

}
