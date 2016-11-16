package weloveclouds.kvstore.deserialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import static weloveclouds.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.RingMetadataPartDeserializer;
import weloveclouds.kvstore.deserialization.helper.ServerInitializationContextDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.models.ServerInitializationContext;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link KVAdminMessage}.
 * 
 * @author Benoit
 */
public class KVAdminMessageDeserializer
        implements IMessageDeserializer<KVAdminMessage, SerializedMessage> {

    private static final int NUMBER_OF_MESSAGE_PARTS = 4;

    private static final int MESSAGE_STATUS_INDEX = 0;
    private static final int MESSAGE_INITIALIZATION_CONTEXT_INDEX = 1;
    private static final int MESSAGE_TARGET_SERVER_INFO_INDEX = 2;
    private static final int MESSAGE_RESPONSE_MESSAGE_INDEX = 3;

    private IDeserializer<RingMetadataPart, String> metadataPartDeserializer =
            new RingMetadataPartDeserializer();
    private IDeserializer<ServerInitializationContext, String> initializationContextDeserializer =
            new ServerInitializationContextDeserializer();

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public KVAdminMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVAdminMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        logger.debug("Deserializing message from byte[].");

        // raw message split
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);
        String[] messageParts = serializedMessageStr.split(KVAdminMessageSerializer.SEPARATOR);

        // length check
        if (messageParts.length != NUMBER_OF_MESSAGE_PARTS) {
            String errorMessage = CustomStringJoiner.join("", "Message must consist of exactly ",
                    String.valueOf(NUMBER_OF_MESSAGE_PARTS), " parts.");
            logger.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }

        try {
            // raw fields
            StatusType status = StatusType.valueOf(messageParts[MESSAGE_STATUS_INDEX]);
            String initializationContextStr = messageParts[MESSAGE_INITIALIZATION_CONTEXT_INDEX];
            String targetServerInfoStr = messageParts[MESSAGE_TARGET_SERVER_INFO_INDEX];
            String responseMessage = messageParts[MESSAGE_RESPONSE_MESSAGE_INDEX];

            // deserialized fields
            ServerInitializationContext initializationContext =
                    initializationContextDeserializer.deserialize(initializationContextStr);
            RingMetadataPart targetServerInfo =
                    metadataPartDeserializer.deserialize(targetServerInfoStr);

            // deserialized object
            KVAdminMessage deserialized = new KVAdminMessage.KVAdminMessageBuilder().status(status)
                    .initializationContext(initializationContext).targetServerInfo(targetServerInfo)
                    .responseMessage(responseMessage).build();

            logger.debug(join(" ", "Deserialized message is:", deserialized.toString()));
            return deserialized;
        } catch (IllegalArgumentException ex) {
            logger.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }
}
