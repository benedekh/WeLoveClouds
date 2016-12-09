package weloveclouds.commons.kvstore.deserialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import static weloveclouds.commons.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.RingMetadataDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.RingMetadataPartDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link KVAdminMessage}.
 * 
 * @author Benedek
 */
public class KVAdminMessageDeserializer
        implements IMessageDeserializer<KVAdminMessage, SerializedMessage> {

    private static final int NUMBER_OF_MESSAGE_PARTS = 4;

    private static final int MESSAGE_STATUS_INDEX = 0;
    private static final int MESSAGE_RING_METADATA_INDEX = 1;
    private static final int MESSAGE_TARGET_SERVER_INFO_INDEX = 2;
    private static final int MESSAGE_RESPONSE_MESSAGE_INDEX = 3;

    private static final Logger LOGGER = Logger.getLogger(KVAdminMessageDeserializer.class);

    private IDeserializer<RingMetadata, String> metadataDeserializer =
            new RingMetadataDeserializer();
    private IDeserializer<RingMetadataPart, String> metadataPartDeserializer =
            new RingMetadataPartDeserializer();;

    @Override
    public KVAdminMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVAdminMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        LOGGER.debug("Deserializing KVAdminMessage from byte[].");

        // remove prefix and postfix
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);
        serializedMessageStr = serializedMessageStr.replace(KVAdminMessageSerializer.PREFIX, "");
        serializedMessageStr = serializedMessageStr.replace(KVAdminMessageSerializer.POSTFIX, "");

        // raw message split
        String[] messageParts = serializedMessageStr.split(KVAdminMessageSerializer.SEPARATOR);

        // length check
        if (messageParts.length != NUMBER_OF_MESSAGE_PARTS) {
            String errorMessage = CustomStringJoiner.join("", "Message must consist of exactly ",
                    String.valueOf(NUMBER_OF_MESSAGE_PARTS), " parts.");
            LOGGER.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }

        try {
            // raw fields
            String statusStr = messageParts[MESSAGE_STATUS_INDEX];
            String ringMetadataStr = messageParts[MESSAGE_RING_METADATA_INDEX];
            String targetServerInfoStr = messageParts[MESSAGE_TARGET_SERVER_INFO_INDEX];
            String responseMessageStr = messageParts[MESSAGE_RESPONSE_MESSAGE_INDEX];

            // deserialized fields
            StatusType status = StatusType.valueOf(statusStr);
            RingMetadata ringMetadata = metadataDeserializer.deserialize(ringMetadataStr);
            RingMetadataPart targetServerInfo =
                    metadataPartDeserializer.deserialize(targetServerInfoStr);
            String responseMessage = "null".equals(responseMessageStr) ? null : responseMessageStr;

            // deserialized object
            KVAdminMessage deserialized = new KVAdminMessage.Builder().status(status)
                    .ringMetadata(ringMetadata).targetServerInfo(targetServerInfo)
                    .responseMessage(responseMessage).build();

            LOGGER.debug(join(" ", "Deserialized KVAdminMessage is:", deserialized.toString()));
            return deserialized;
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }
}
