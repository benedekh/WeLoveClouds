package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadataPart;

/**
 * A serializer which converts a {@link RingMetadataPart} to a {@link String}.
 * 
 * @author Benedek
 */
public class RingMetadataPartSerializer implements ISerializer<String, RingMetadataPart> {

    public static final String SEPARATOR = "-\r\t-";
    private static final Logger LOGGER = Logger.getLogger(RingMetadataPartSerializer.class);

    private ISerializer<String, ServerConnectionInfo> connectionInfoSerializer =
            new ServerConnectionInfoSerializer();
    private ISerializer<String, HashRange> hashRangeSerializer = new HashRangeSerializer();

    @Override
    public String serialize(RingMetadataPart target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a RingMetadataPart.");
            serialized = target.toStringWithDelimiter(SEPARATOR, connectionInfoSerializer,
                    hashRangeSerializer);
            LOGGER.debug("Serializing a RingMetadataPart finished.");
        }

        return serialized;
    }

}
