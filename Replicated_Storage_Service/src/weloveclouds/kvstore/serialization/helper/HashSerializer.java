package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.hashing.models.Hash;

/**
 * A serializer which converts a {@link Hash} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashSerializer implements ISerializer<String, Hash> {

    public static final String SEPARATOR_INSIDE_HASH = "-ł-";
    private static final Logger LOGGER = Logger.getLogger(HashSerializer.class);

    @Override
    public String serialize(Hash target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a Hash.");
            serialized = target.toStringWithDelimiter(SEPARATOR_INSIDE_HASH);
            LOGGER.debug("Serializing a Hash finished.");
        }

        return serialized;
    }

}
