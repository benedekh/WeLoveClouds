package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;

/**
 * A serializer which converts a {@link HashRange} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashRangeSerializer implements ISerializer<String, HashRange> {

    public static final String SEPARATOR = "-Ł-";
    private static final Logger LOGGER = Logger.getLogger(HashRangeSerializer.class);

    private ISerializer<String, Hash> hashSerializer = new HashSerializer();

    @Override
    public String serialize(HashRange target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a HashRange.");
            // original fields
            Hash begin = target.getStart();
            Hash end = target.getEnd();

            // string representation
            String beginStr = hashSerializer.serialize(begin);
            String endStr = hashSerializer.serialize(end);

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, beginStr, endStr);
            LOGGER.debug("Serializing a HashRange finished.");
        }

        return serialized;
    }

}
