package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.HashRanges;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.RingMetadataPartSerializer;

/**
 * A deserializer which converts a {@link RingMetadataPart} to a {@link String}.
 * 
 * @author Benedek
 */
public class RingMetadataPartDeserializer implements IDeserializer<RingMetadataPart, String> {

    private static final int NUMBER_OF_RING_METADATA_PART_PARTS = 3;
    private static final int CONNECTION_INFO_INDEX = 0;
    private static final int READ_RANGES_INDEX = 1;
    private static final int WRITE_RANGES_INDEX = 2;

    private static final Logger LOGGER = Logger.getLogger(RingMetadataPartDeserializer.class);

    private IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();
    private IDeserializer<HashRanges, String> hashRangesDeserializer = new HashRangesDeserializer();
    private IDeserializer<HashRange, String> hashRangeDeserializer = new HashRangeDeserializer();

    @Override
    public RingMetadataPart deserialize(String from) throws DeserializationException {
        RingMetadataPart deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a RingMetadataPart from String.");
            // raw message split
            String[] parts = from.split(RingMetadataPartSerializer.SEPARATOR);

            // length check
            if (parts.length != NUMBER_OF_RING_METADATA_PART_PARTS) {
                String errorMessage =
                        CustomStringJoiner.join("", "Ring metadata part must consist of exactly ",
                                String.valueOf(NUMBER_OF_RING_METADATA_PART_PARTS), " parts.");
                LOGGER.debug(errorMessage);
                throw new DeserializationException(errorMessage);
            }

            // raw fields
            String connectionInfoStr = parts[CONNECTION_INFO_INDEX];
            String readRangesStr = parts[READ_RANGES_INDEX];
            String writeRangeStr = parts[WRITE_RANGES_INDEX];

            // deserialized fields
            ServerConnectionInfo connectionInfo =
                    connectionInfoDeserializer.deserialize(connectionInfoStr);
            HashRanges readRanges = hashRangesDeserializer.deserialize(readRangesStr);
            HashRange writeRange = hashRangeDeserializer.deserialize(writeRangeStr);

            // deserialized object
            deserialized = new RingMetadataPart.Builder().connectionInfo(connectionInfo)
                    .readRanges(readRanges).writeRange(writeRange).build();
            LOGGER.debug(join(" ", "Deserialized metadata part is:", deserialized.toString()));
        }

        return deserialized;
    }

}
