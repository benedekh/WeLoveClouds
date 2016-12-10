package weloveclouds.kvstore.serialization.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.HashRanges;

/**
 * A serializer which converts a {@link HashRangesSerializer} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashRangesSerializer implements ISerializer<String, HashRanges> {

    public static final String SEPARATOR = "-łŁ-";
    private static final Logger LOGGER = Logger.getLogger(HashRangesSerializer.class);

    private ISerializer<String, HashRange> hashRangeSerializer = new HashRangeSerializer();

    @Override
    public String serialize(HashRanges target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a HashRanges.");
            // original fields
            Set<HashRange> ranges = target.getHashRanges();

            // string representation
            Set<String> rangesStrs = new HashSet<>();
            for (HashRange range : ranges) {
                rangesStrs.add(hashRangeSerializer.serialize(range));
            }

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, new ArrayList<>(rangesStrs));
            LOGGER.debug("Serializing a HashRanges finished.");
        }

        return serialized;
    }
}
