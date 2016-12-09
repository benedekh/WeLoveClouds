package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.KVEntry;

/**
 * A serializer which converts a {@link KVEntry} to a {@link String}.
 * 
 * @author Benedek
 */
public class KVEntrySerializer implements ISerializer<String, KVEntry> {

    public static final String SEPARATOR = "-ł-";
    private static final Logger LOGGER = Logger.getLogger(KVEntrySerializer.class);

    @Override
    public String serialize(KVEntry target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a KVEntry.");
            // original fields
            String key = target.getKey();
            String value = target.getValue();

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, key, value);
            LOGGER.debug("Serializing a KVEntry finished.");
        }

        return serialized;
    }

}
