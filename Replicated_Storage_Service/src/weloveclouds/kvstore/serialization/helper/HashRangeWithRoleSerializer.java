package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.hashing.models.HashRange;
import weloveclouds.server.models.replication.HashRangeWithRole;

public class HashRangeWithRoleSerializer implements ISerializer<String, HashRangeWithRole> {

    public static final String SEPARATOR = "-łŁ-";
    private static final Logger LOGGER = Logger.getLogger(HashRangeWithRoleSerializer.class);

    private ISerializer<String, HashRange> rangeSerializer = new HashRangeSerializer();

    @Override
    public String serialize(HashRangeWithRole target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a HashRangeWithRole.");
            serialized = target.toStringWithDelimiter(SEPARATOR, rangeSerializer);
            LOGGER.debug("Serializing a HashRangeWithRole finished.");
        }

        return serialized;
    }

}
