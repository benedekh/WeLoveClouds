package weloveclouds.client.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.client.utils.PutCommandUtils;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.models.messages.IKVMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;

/**
 * Put command which means the client would like to send a key along with a value to the server.
 *
 * @author Benoit, Benedek, Hunton
 */
public class Put extends AbstractKVCommunicationApiCommand {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final Logger LOGGER = Logger.getLogger(Put.class);

    private IDeserializer<RingMetadata, String> ringMetadataDeserializer;
    private IKVCommunicationApiV2 communicationApiV2;

    private boolean commandWasAlreadyExecuted;

    /**
     * @param arguments contains the key in the {@link #KEY_INDEX} position and the value is merged
     *        into one value starting from the index {@link #VALUE_INDEX} and going until the end of
     *        the array
     * @param communicationApi which is used for querying the value from the server
     * @param ringMetadataDeserializer deserializer that converts a {@link RingMetadata} object to
     *        its original representation from String
     */
    public Put(String[] arguments, IKVCommunicationApiV2 communicationApi,
            IDeserializer<RingMetadata, String> ringMetadataDeserializer) {
        super(arguments, communicationApi);
        this.ringMetadataDeserializer = ringMetadataDeserializer;
        this.communicationApiV2 = communicationApi;
        this.commandWasAlreadyExecuted = false;
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            LOGGER.info("Executing put command.");
            String key = arguments[KEY_INDEX];
            String value = PutCommandUtils.mergeValuesToOneString(VALUE_INDEX, arguments);

            IKVMessage response = communicationApi.put(key, value);
            LOGGER.debug(response.toString());

            switch (response.getStatus()) {
                case PUT_UPDATE:
                    userOutputWriter.writeLine("Key was successfully updated on the server.");
                    break;
                case PUT_SUCCESS:
                    userOutputWriter.writeLine("Key was successfully put on the server.");
                    break;
                case PUT_ERROR:
                    userOutputWriter.writeLine(CustomStringJoiner.join(" ", "Error during key put:",
                            response.getValue()));
                    break;
                case DELETE_SUCCESS:
                    userOutputWriter.writeLine("Key removed successfully.");
                    break;
                case DELETE_ERROR:
                    userOutputWriter
                            .writeLine(join(" ", "Error during key remove:", response.getValue()));
                    break;
                case SERVER_NOT_RESPONSIBLE:
                    try {
                        LOGGER.error(join(" ", "Server is not responsible for the key:", key,
                                ". Updating ring metadata information."));

                        RingMetadata ringMetadata =
                                ringMetadataDeserializer.deserialize(response.getValue());
                        communicationApiV2.setRingMetadata(ringMetadata);

                        if (!commandWasAlreadyExecuted) {
                            commandWasAlreadyExecuted = true;
                            execute();
                        } else {
                            String errorMessage =
                                    "Put command execution failed, because responsible server was not found.";
                            LOGGER.error(errorMessage);
                            throw new ClientSideException(errorMessage);
                        }
                    } catch (DeserializationException e) {
                        LOGGER.error(e);
                        userOutputWriter.writeLine(
                                "Error during key PUT. The respective server cannot handle the key.");
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
                    userOutputWriter.writeLine("Unexpected response type.");
                    break;
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new ClientSideException(e.getMessage(), e);
        } finally {
            LOGGER.info("Put command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validatePutArguments(arguments);
        return this;
    }


}
