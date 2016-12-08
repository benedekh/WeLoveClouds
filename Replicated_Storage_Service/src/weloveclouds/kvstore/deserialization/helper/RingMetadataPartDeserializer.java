package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.RingMetadataPartSerializer;
import weloveclouds.server.models.replication.HashRangeWithRole;

/**
 * A deserializer which converts a {@link RingMetadataPart} to a {@link String}.
 * 
 * @author Benedek
 */
public class RingMetadataPartDeserializer implements IDeserializer<RingMetadataPart, String> {

    private static final int NUMBER_OF_RING_METADATA_PART_PARTS = 2;
    private static final int CONNECTION_INFO_INDEX = 0;
    private static final int RANGE_WITH_ROLE_INDEX = 1;

    private static final Logger LOGGER = Logger.getLogger(RingMetadataPartDeserializer.class);

    private IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();
    private IDeserializer<HashRangeWithRole, String> hashRangeWithRoleDeserializer =
            new HashRangeWithRoleDeserializer();

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
            String rangeWithRoleStr = parts[RANGE_WITH_ROLE_INDEX];

            // deserialized fields
            ServerConnectionInfo connectionInfo =
                    connectionInfoDeserializer.deserialize(connectionInfoStr);
            HashRangeWithRole rangeWithRole =
                    hashRangeWithRoleDeserializer.deserialize(rangeWithRoleStr);

            // deserialized object
            deserialized = new RingMetadataPart.Builder().connectionInfo(connectionInfo)
                    .rangeWithRole(rangeWithRole).build();
            LOGGER.debug(join(" ", "Deserialized metadata part is:", deserialized.toString()));
        }

        return deserialized;
    }

}
