package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashRangeWithRoleSerializer;
import weloveclouds.server.models.replication.HashRangeWithRole;
import weloveclouds.server.models.replication.Role;

/**
 * A deserializer which converts a {@link HashRangeWithRole} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashRangeWithRoleDeserializer implements IDeserializer<HashRangeWithRole, String> {

    private static final int NUMBER_OF_RANGE_INFO_PARTS = 2;
    private static final int RANGE_INDEX = 0;
    private static final int ROLE_INDEX = 1;

    private static final Logger LOGGER = Logger.getLogger(RingMetadataPartDeserializer.class);

    private IDeserializer<HashRange, String> hashRangeDeserializer = new HashRangeDeserializer();

    @Override
    public HashRangeWithRole deserialize(String from) throws DeserializationException {
        HashRangeWithRole deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a HashRangeWithRole from String.");
            // raw message split
            String[] parts = from.split(HashRangeWithRoleSerializer.SEPARATOR);

            // length check
            if (parts.length != NUMBER_OF_RANGE_INFO_PARTS) {
                String errorMessage =
                        CustomStringJoiner.join("", "Hash range with role must consist of exactly ",
                                String.valueOf(NUMBER_OF_RANGE_INFO_PARTS), " parts.");
                LOGGER.debug(errorMessage);
                throw new DeserializationException(errorMessage);
            }

            // raw fields
            String rangeStr = parts[RANGE_INDEX];
            String roleStr = parts[ROLE_INDEX];

            // deserialized fields
            HashRange range = hashRangeDeserializer.deserialize(rangeStr);
            Role role = "null".equals(roleStr) ? null : Role.valueOf(roleStr);

            // deserialized object
            deserialized = new HashRangeWithRole.Builder().hashRange(range).role(role).build();
            LOGGER.debug(
                    join(" ", "Deserialized hash range with role is:", deserialized.toString()));
        }

        return deserialized;
    }

}
