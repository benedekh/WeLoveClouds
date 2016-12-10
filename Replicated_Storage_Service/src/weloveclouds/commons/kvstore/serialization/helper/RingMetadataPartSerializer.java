package weloveclouds.commons.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadataPart;

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
    private ISerializer<String, HashRange> hashRangeSerializer = new HashRangeSerializer();

    @Override
    public String serialize(RingMetadataPart target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a RingMetadataPart.");
            // original fields
            ServerConnectionInfo connectionInfo = target.getConnectionInfo();
            HashRange hashRange = target.getRange();

            // string representation
            String connectionInfoStr = connectionInfoSerializer.serialize(connectionInfo);
            String hashRangeStr = hashRangeSerializer.serialize(hashRange);

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, connectionInfoStr, hashRangeStr);
            LOGGER.debug("Serializing a RingMetadataPart finished.");
        }

        return serialized;
    }

}
