package weloveclouds.client.commands;

import org.apache.log4j.Logger;

import weloveclouds.client.commands.utils.ArgumentsValidator;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;


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

    private boolean commandWasAlreadyExecuted;

    protected Get(Builder builder) {
        super(builder.arguments, builder.communicationApi);
        this.ringMetadataDeserializer = builder.ringMetadataDeserializer;
        this.communicationApiV2 = builder.communicationApi;
        this.commandWasAlreadyExecuted = false;
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
                    userOutputWriter.writeLine(StringUtils.join(" ", "Value", responseValue,
                            "was sucessfully got for key."));
                    break;
                case GET_ERROR:
                    userOutputWriter.writeLine(
                            StringUtils.join(" ", "Error during key get:", responseValue));
                    break;
                case SERVER_NOT_RESPONSIBLE:
                    try {
                        LOGGER.error(StringUtils.join(" ", "Server is not responsible for the key:",
                                key, ". Updating ring metadata information."));

                        RingMetadata ringMetadata =
                                ringMetadataDeserializer.deserialize(response.getValue());
                        communicationApiV2.setRingMetadata(ringMetadata);

                        if (!commandWasAlreadyExecuted) {
                            commandWasAlreadyExecuted = true;
                            execute();
                        } else {
                            String errorMessage =
                                    "Get command execution failed, because responsible server was not found.";
                            LOGGER.error(errorMessage);
                            throw new ClientSideException(errorMessage);
                        }

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

    /**
     * Builder pattern for creating a {@link Get} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private String[] arguments;
        private IKVCommunicationApiV2 communicationApi;
        private IDeserializer<RingMetadata, String> ringMetadataDeserializer;

        /**
         * @param arguments contains the key in the {@value #KEY_INDEX} position
         */
        public Builder arguments(String[] arguments) {
            this.arguments = arguments;
            return this;
        }

        /**
         * @param communicationApi which is used for querying the value from the server
         */
        public Builder communicationApi(IKVCommunicationApiV2 communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        /**
         * @param ringMetadataDeserializer deserializer that converts a {@link RingMetadata} object
         *        to its original representation from String
         */
        public Builder ringMetadataDeserializer(
                IDeserializer<RingMetadata, String> ringMetadataDeserializer) {
            this.ringMetadataDeserializer = ringMetadataDeserializer;
            return this;
        }

        public Get build() {
            return new Get(this);
        }
    }

}
