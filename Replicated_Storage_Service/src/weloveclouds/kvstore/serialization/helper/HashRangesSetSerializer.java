package weloveclouds.kvstore.serialization.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;

/**
 * A serializer which converts a {@link Set<HashRange>} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashRangesSetSerializer implements ISerializer<String, Set<HashRange>> {

    public static final String SEPARATOR = "-łŁ-";
    private static final Logger LOGGER = Logger.getLogger(HashRangesSetSerializer.class);

    private ISerializer<String, HashRange> hashRangeSerializer = new HashRangeSerializer();

    @Override
    public String serialize(Set<HashRange> target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a Set<HashRange>.");
            // string representation
            Set<String> rangesStrs = new HashSet<>();
            for (HashRange range : target) {
                rangesStrs.add(hashRangeSerializer.serialize(range));
            }

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, new ArrayList<>(rangesStrs));
            LOGGER.debug("Serializing a Set<HashRange> finished.");
        }

        return serialized;
    }
}
