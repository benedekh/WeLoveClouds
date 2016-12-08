package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.server.models.replication.HashRangeWithRole;
import weloveclouds.server.models.replication.HashRangesWithRoles;

/**
 * A serializer which converts a {@link HashRangesWithRoles} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashRangesWithRolesSerializer implements ISerializer<String, HashRangesWithRoles> {

    public static final String SEPARATOR = "-łł-";
    private static final Logger LOGGER = Logger.getLogger(HashRangesWithRolesSerializer.class);

    private ISerializer<String, HashRangeWithRole> hashRangeWithRoleSerializer =
            new HashRangeWithRoleSerializer();

    @Override
    public String serialize(HashRangesWithRoles target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a HashRangesWithRoles.");
            serialized = target.toStringWithDelimiter(SEPARATOR, hashRangeWithRoleSerializer);
            LOGGER.debug("Serializing a HashRangesWithRoles finished.");
        }

        return serialized;
    }
}
