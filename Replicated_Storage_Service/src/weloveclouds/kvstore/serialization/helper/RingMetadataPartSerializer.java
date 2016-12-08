package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.server.models.replication.HashRangeWithRole;

/**
 * A serializer which converts a {@link RingMetadataPart} to a {@link String}.
 * 
 * @author Benedek
 */
public class RingMetadataPartSerializer implements ISerializer<String, RingMetadataPart> {

    public static final String SEPARATOR = "-Łł-";
    private static final Logger LOGGER = Logger.getLogger(RingMetadataPartSerializer.class);

    private ISerializer<String, ServerConnectionInfo> connectionInfoSerializer =
            new ServerConnectionInfoSerializer();
    private ISerializer<String, HashRangeWithRole> hashRangeWithRoleSerializer =
            new HashRangeWithRoleSerializer();

    @Override
    public String serialize(RingMetadataPart target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a RingMetadataPart.");
            serialized = target.toStringWithDelimiter(SEPARATOR, connectionInfoSerializer,
                    hashRangeWithRoleSerializer);
            LOGGER.debug("Serializing a RingMetadataPart finished.");
        }

        return serialized;
    }

}
