package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import static weloveclouds.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RangeInfo;
import weloveclouds.hashing.models.RangeInfos;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * An exact deserializer which converts a {@link SerializedMessage} to a {@link KVAdminMessage}.
 * 
 * @author Benoit
 */
public class KVAdminMessageDeserializer
        implements IMessageDeserializer<KVAdminMessage, SerializedMessage> {

    private static String SEPARATOR = "-\r-";
    private static int NUMBER_OF_MESSAGE_PARTS = 4;
    private static int NUMBER_OF_RANGE_INFO_PARTS = 2;
    private static int NUMBER_OF_CONNECTION_INFO_PARTS = 2;
    private static int NUMBER_OF_HASH_RANGE_PARTS = 2;
    private static int NUMBER_OF_HASH_PARTS = 128;

    private static int MESSAGE_STATUS_INDEX = 0;
    private static int MESSAGE_RING_METADATA_INDEX = 1;
    private static int MESSAGE_TARGET_SERVER_INFO_INDEX = 2;
    private static int MESSAGE_RESPONSE_MESSAGE_INDEX = 3;

    private static int CONNECTION_INFO_INDEX = 0;
    private static int IP_ADDRESS_INDEX = 0;
    private static int PORT_INDEX = 1;

    private static int RANGE_INDEX = 1;
    private static int RANGE_START_INDEX = 0;
    private static int RANGE_END_INDEX = 1;

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
        String[] messageParts = serializedMessageStr.split(SEPARATOR);

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
            String ringMetadataStr = messageParts[MESSAGE_RING_METADATA_INDEX];
            String targetServerInfoStr = messageParts[MESSAGE_TARGET_SERVER_INFO_INDEX];
            String responseMessage = messageParts[MESSAGE_RESPONSE_MESSAGE_INDEX];

            // deserialized fields
            RangeInfos ringMetadata = "null".equals(ringMetadataStr) ? null
                    : deserializeRingMetadata(ringMetadataStr);
            RangeInfo targetServerInfo = "null".equals(targetServerInfoStr) ? null
                    : deserializeTargetServerInfo(targetServerInfoStr);

            // deserialized object
            KVAdminMessage deserialized = new KVAdminMessage.KVAdminMessageBuilder().status(status)
                    .ringMetadata(ringMetadata).targetServerInfo(targetServerInfo)
                    .responseMessage(responseMessage).build();

            logger.debug(join(" ", "Deserialized message is:", deserialized.toString()));
            return deserialized;
        } catch (IllegalArgumentException ex) {
            logger.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }

    /**
     * Deserialize a {@link RangeInfo} from its string serialized version, which was probably
     * created by {@link RangeInfo#toStringWithDelimiter()}.
     * 
     * @throws DeserializationException if a deserialization error occurs
     */
    private RangeInfo deserializeTargetServerInfo(String targetServerInfoStr)
            throws DeserializationException {
        // raw message split
        String[] parts = targetServerInfoStr.split(RangeInfo.FIELD_DELIMITER);

        // length check
        if (parts.length != NUMBER_OF_RANGE_INFO_PARTS) {
            String errorMessage = CustomStringJoiner.join("", "Range info must consist of exactly ",
                    String.valueOf(NUMBER_OF_RANGE_INFO_PARTS), " parts.");
            logger.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }

        // raw fields
        String connectionInfoStr = parts[CONNECTION_INFO_INDEX];
        String hashRangeStr = parts[RANGE_INDEX];

        // deserialized fields
        ServerConnectionInfo connectionInfo = "null".equals(connectionInfoStr) ? null
                : deserializeConnectionInfo(connectionInfoStr);
        HashRange range = "null".equals(hashRangeStr) ? null : deserializeHashRange(hashRangeStr);

        // deserialized object
        RangeInfo deserialized = new RangeInfo.RangeInfoBuilder().connectionInfo(connectionInfo)
                .range(range).build();

        logger.debug(join(" ", "Deserialized range info is:", deserialized.toString()));
        return deserialized;
    }

    /**
     * Deserialize a {@link ServerConnectionInfo} from its string serialized version, which was
     * probably created by {@link ServerConnectionInfo#toStringWithDelimiter()}.
     * 
     * @throws DeserializationException if a deserialization error occurs
     */
    private ServerConnectionInfo deserializeConnectionInfo(String connectionInfoStr)
            throws DeserializationException {
        // raw message split
        String[] parts = connectionInfoStr.split(ServerConnectionInfo.FIELD_DELIMITER);

        // length check
        if (parts.length != NUMBER_OF_CONNECTION_INFO_PARTS) {
            String errorMessage =
                    CustomStringJoiner.join("", "Connection info must consist of exactly ",
                            String.valueOf(NUMBER_OF_CONNECTION_INFO_PARTS), " parts.");
            logger.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }

        // raw fields
        String ipAddress = parts[IP_ADDRESS_INDEX];
        String portStr = parts[PORT_INDEX];

        try {
            // deserialized fields
            int port = Integer.valueOf(portStr);

            // deserialized object
            ServerConnectionInfo deserialized =
                    new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress(ipAddress)
                            .port(port).build();

            logger.debug(join(" ", "Deserialized connection info is:", deserialized.toString()));
            return deserialized;
        } catch (NumberFormatException ex) {
            String errorMessage = CustomStringJoiner.join(": ", "Port is NaN", parts[PORT_INDEX]);
            logger.error(errorMessage);
            throw new DeserializationException(errorMessage);
        } catch (UnknownHostException ex) {
            String errorMessage = CustomStringJoiner.join(": ",
                    "Host referred by IP address is unknown", ipAddress);
            logger.error(errorMessage);
            throw new DeserializationException(errorMessage);
        }
    }

    /**
     * Deserialize a {@link HashRange} from its string serialized version, which was probably
     * created by {@link HashRange#toStringWithDelimiter()}.
     * 
     * @throws DeserializationException if a deserialization error occurs
     */
    private HashRange deserializeHashRange(String hashRangeStr) throws DeserializationException {
        // raw message split
        String[] parts = hashRangeStr.split(HashRange.FIELD_DELIMITER);

        // length check
        if (parts.length != NUMBER_OF_HASH_RANGE_PARTS) {
            String errorMessage = CustomStringJoiner.join("", "Hash range must consist of exactly ",
                    String.valueOf(NUMBER_OF_HASH_RANGE_PARTS), " parts.");
            logger.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }

        // raw fields
        String startHashStr = parts[RANGE_START_INDEX];
        String endHashStr = parts[RANGE_END_INDEX];

        // deserialized fields
        Hash startHash = "null".equals(startHashStr) ? null : deserializeHash(startHashStr);
        Hash endHash = "null".equals(endHashStr) ? null : deserializeHash(endHashStr);

        // deserialized object
        HashRange deserialized = new HashRange(startHash, endHash);

        logger.debug(join(" ", "Deserialized hash range is:", deserialized.toString()));
        return deserialized;
    }

    /**
     * Deserialize a {@link Hash} from its string serialized version, which was probably created by
     * {@link Hash#toStringWithDelimiter()}.
     * 
     * @throws DeserializationException if a deserialization error occurs
     */
    private Hash deserializeHash(String hashStr) throws DeserializationException {
        // raw message split
        String[] parts = hashStr.split(Hash.DELIMITER);

        // length check
        if (parts.length != NUMBER_OF_HASH_PARTS) {
            String errorMessage = CustomStringJoiner.join("", "Hash must consist of exactly ",
                    String.valueOf(NUMBER_OF_HASH_PARTS), " parts.");
            logger.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }

        int i = 0;
        try {
            // deserialized fields
            byte[] hash = new byte[NUMBER_OF_HASH_PARTS];
            for (i = 0; i < NUMBER_OF_HASH_PARTS; ++i) {
                hash[i] = Byte.valueOf(parts[i]);
            }

            // deserialized object
            Hash deserialized = new Hash(hash);

            logger.debug(join(" ", "Deserialized hash is:", deserialized.toString()));
            return deserialized;
        } catch (NumberFormatException ex) {
            String errorMessage =
                    CustomStringJoiner.join("", "Deserialized hash byte segment at index ",
                            String.valueOf(i), " is not a byte: ", parts[i]);
            logger.debug(errorMessage);
            throw new DeserializationException(errorMessage);
        }
    }

    /**
     * Deserialize a {@link RangeInfos} from its string serialized version, which was probably
     * created by {@link RangeInfos#toStringWithDelimiter()}.
     * 
     * @throws DeserializationException if a deserialization error occurs
     */
    private RangeInfos deserializeRingMetadata(String ringMetadataStr)
            throws DeserializationException {
        // raw message split
        String[] parts = ringMetadataStr.split(RangeInfos.FIELD_DELIMITER);

        // deserialized fields
        Set<RangeInfo> rangeInfos = new HashSet<>();
        for (String serializedRangeInfo : parts) {
            rangeInfos.add(deserializeTargetServerInfo(serializedRangeInfo));
        }

        // deserialized object
        RangeInfos deserialized = new RangeInfos(rangeInfos);
        
        logger.debug(join(" ", "Deserialized ring metadata is:", deserialized.toString()));
        return deserialized;
    }

}
