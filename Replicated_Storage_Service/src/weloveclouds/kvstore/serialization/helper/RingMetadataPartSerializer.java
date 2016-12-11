package weloveclouds.kvstore.serialization.helper;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadataPart;

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
    private ISerializer<String, Set<HashRange>> hashRangesSerializer =
            new HashRangesSetSerializer();
    private ISerializer<String, HashRange> hashRangeSerializer = new HashRangeSerializer();

    @Override
    public String serialize(RingMetadataPart target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a RingMetadataPart.");
            // original fields
            ServerConnectionInfo connectionInfo = target.getConnectionInfo();
            Set<HashRange> readRanges = target.getReadRanges();
            HashRange writeRange = target.getWriteRange();

            // string representation
            String connectionInfoStr = connectionInfoSerializer.serialize(connectionInfo);
            String readRangesStr = hashRangesSerializer.serialize(readRanges);
            String writeRangeStr = hashRangeSerializer.serialize(writeRange);

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, connectionInfoStr, readRangesStr,
                    writeRangeStr);
            LOGGER.debug("Serializing a RingMetadataPart finished.");
        }

        return serialized;
    }

}
