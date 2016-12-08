package weloveclouds.kvstore.serialization.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
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
            // original fields
            Set<HashRangeWithRole> rangesWithRoles = target.getRangesWithRoles();

            // string representation
            Set<String> rangesWithRolesStrs = new HashSet<>();
            for (HashRangeWithRole rangeWithRole : rangesWithRoles) {
                rangesWithRolesStrs.add(hashRangeWithRoleSerializer.serialize(rangeWithRole));
            }

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, new ArrayList<>(rangesWithRolesStrs));
            LOGGER.debug("Serializing a HashRangesWithRoles finished.");
        }

        return serialized;
    }
}
