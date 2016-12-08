package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.server.models.replication.HashRangeWithRole;
import weloveclouds.server.models.replication.Role;

/**
 * A serializer which converts a {@link HashRangeWithRole} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashRangeWithRoleSerializer implements ISerializer<String, HashRangeWithRole> {

    public static final String SEPARATOR = "-łŁ-";
    private static final Logger LOGGER = Logger.getLogger(HashRangeWithRoleSerializer.class);

    private ISerializer<String, HashRange> rangeSerializer = new HashRangeSerializer();

    @Override
    public String serialize(HashRangeWithRole target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a HashRangeWithRole.");
            // original fields
            HashRange hashRange = target.getHashRange();
            Role role = target.getRole();

            // string representation
            String hashRangeStr = rangeSerializer.serialize(hashRange);
            String roleStr = role == null ? "null" : role.toString();

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, hashRangeStr, roleStr);
            LOGGER.debug("Serializing a HashRangeWithRole finished.");
        }

        return serialized;
    }

}
