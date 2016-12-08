package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.RingMetadataSerializer;
import weloveclouds.server.models.replication.HashRangeWithRole;
import weloveclouds.server.models.replication.HashRangesWithRoles;

/**
 * A deserializer which converts a {@link HashRangesWithRoles} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashRangesWithRolesDeserializer implements IDeserializer<HashRangesWithRoles, String> {

    private static final Logger LOGGER = Logger.getLogger(HashRangesWithRolesDeserializer.class);

    private IDeserializer<HashRangeWithRole, String> hashRangeWithRoleDeserializer =
            new HashRangeWithRoleDeserializer();

    @Override
    public HashRangesWithRoles deserialize(String from) throws DeserializationException {
        HashRangesWithRoles deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a HashRangesWithRoles from String.");
            // raw message split
            String[] parts = from.split(RingMetadataSerializer.SEPARATOR);

            // deserialized fields
            Set<HashRangeWithRole> rangesWithRoles = new HashSet<>();
            for (String serializedRangeWithRole : parts) {
                rangesWithRoles
                        .add(hashRangeWithRoleDeserializer.deserialize(serializedRangeWithRole));
            }

            // deserialized object
            deserialized = new HashRangesWithRoles(rangesWithRoles);
            LOGGER.debug(
                    join(" ", "Deserialized HashRangesWithRoles is:", deserialized.toString()));
        }

        return deserialized;
    }

}
