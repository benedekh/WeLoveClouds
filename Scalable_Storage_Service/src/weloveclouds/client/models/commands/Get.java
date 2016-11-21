package weloveclouds.client.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.v2.IKVCommunicationApiV2;
import weloveclouds.communication.exceptions.ClientSideException;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.models.messages.IKVMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;

/**
 * Get command which means the client would like to query the value for a respective key.
 *
 * @author Benoit, Benedek, Hunton
 */
public class Get extends AbstractKVCommunicationApiCommand {

    private static final int KEY_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(Get.class);

    private IDeserializer<RingMetadata, String> ringMetadataDeserializer;
    private IKVCommunicationApiV2 communicationApiV2;

    /**
     * @param arguments contains the key in the {@link #KEY_INDEX} position
     * @param communicationApi which is used for querying the value from the server
     * @param ringMetadataDeserializer deserializer that converts a {@link RingMetadata} object to
     *        its original representation from String
     */
    public Get(String[] arguments, IKVCommunicationApiV2 communicationApi,
            IDeserializer<RingMetadata, String> ringMetadataDeserializer) {
        super(arguments, communicationApi);
        this.ringMetadataDeserializer = ringMetadataDeserializer;
        this.communicationApiV2 = communicationApi;
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            LOGGER.info("Executing get command.");
            String key = arguments[KEY_INDEX];

            IKVMessage response = communicationApi.get(key);
            LOGGER.debug(response.toString());

            String responseValue = response.getValue();
            switch (response.getStatus()) {
                case GET_SUCCESS:
                    userOutputWriter.writeLine(CustomStringJoiner.join(" ", "Value", responseValue,
                            "was sucessfully got for key."));
                    break;
                case GET_ERROR:
                    userOutputWriter.writeLine(join(" ", "Error during key get:", responseValue));
                    break;
                case SERVER_NOT_RESPONSIBLE:
                    try {
                        LOGGER.error(join(" ", "Server is not responsible for the key:", key,
                                ". Updating ring metadata information."));
                        RingMetadata ringMetadata =
                                ringMetadataDeserializer.deserialize(response.getValue());
                        communicationApiV2.setRingMetadata(ringMetadata);
                        execute();
                    } catch (DeserializationException e) {
                        LOGGER.error(e);
                        userOutputWriter.writeLine(
                                "Error during key GET. The respective server cannot handle the key.");
                    }
                    break;
                case SERVER_WRITE_LOCK:
                    LOGGER.error("Write lock is active on the server.");
                    userOutputWriter
                            .writeLine("Server is locked for PUT operations. Try again later.");
                    break;
                case SERVER_STOPPED:
                    LOGGER.error("Server stopped.");
                    userOutputWriter
                            .writeLine("Server is stopped for serving requests. Try again later.");
                    break;
                default:
                    LOGGER.error("Unexpected response type.");
                    userOutputWriter.writeLine("Unexpected response type.");
                    break;
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new ClientSideException(e.getMessage(), e);
        } finally {
            LOGGER.info("Get command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateGetArguments(arguments);
        return this;
    }

}
