package weloveclouds.commons.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;

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
            serialized = target.toStringWithDelimiter(SEPARATOR, hashSerializer);
            LOGGER.debug("Serializing a HashRange finished.");
        }

        return serialized;
    }

}
