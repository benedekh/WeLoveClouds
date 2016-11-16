package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.RingMetadataSerializer;

public class RingMetadataPartDeserializer implements IDeserializer<RingMetadataPart, String> {

    private static final int NUMBER_OF_RANGE_INFO_PARTS = 2;

    private static final int CONNECTION_INFO_INDEX = 0;
    private static final int RANGE_INDEX = 1;

    private IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();
    private IDeserializer<HashRange, String> hashRangeDeserializer = new HashRangeDeserializer();

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public RingMetadataPart deserialize(String from) throws DeserializationException {
        RingMetadataPart deserialized = null;

        if (from != null) {
            // raw message split
            String[] parts = from.split(RingMetadataSerializer.SEPARATOR);

            // length check
            if (parts.length != NUMBER_OF_RANGE_INFO_PARTS) {
                String errorMessage =
                        CustomStringJoiner.join("", "Ring metadata part must consist of exactly ",
                                String.valueOf(NUMBER_OF_RANGE_INFO_PARTS), " parts.");
                logger.debug(errorMessage);
                throw new DeserializationException(errorMessage);
            }

            // raw fields
            String connectionInfoStr = parts[CONNECTION_INFO_INDEX];
            String hashRangeStr = parts[RANGE_INDEX];

            // deserialized fields
            ServerConnectionInfo connectionInfo = "null".equals(connectionInfoStr) ? null
                    : connectionInfoDeserializer.deserialize(connectionInfoStr);
            HashRange range = hashRangeDeserializer.deserialize(hashRangeStr);

            // deserialized object
            deserialized = new RingMetadataPart.RingMetadataPartBuilder()
                    .connectionInfo(connectionInfo).range(range).build();
        }

        logger.debug(join(" ", "Deserialized metadata part is:", deserialized.toString()));
        return deserialized;
    }

}
