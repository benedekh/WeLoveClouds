package weloveclouds.commons.kvstore.deserialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.serialization.helper.KVEntrySerializer;

/**
 * A deserializer which converts a {@link KVEntry} to a {@link String}.
 * 
 * @author Benedek
 */
public class KVEntryDeserializer implements IDeserializer<KVEntry, String> {

    private static final int NUMBER_OF_KVENTRY_PARTS = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private static final Logger LOGGER = Logger.getLogger(KVEntryDeserializer.class);

    @Override
    public KVEntry deserialize(String from) throws DeserializationException {
        KVEntry deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a KVEntry from String.");
            // raw message split
            String[] parts = from.split(KVEntrySerializer.SEPARATOR);

            // length check
            if (parts.length != NUMBER_OF_KVENTRY_PARTS) {
                throw new DeserializationException(
                        CustomStringJoiner.join("", "KVEntry must consist of exactly ",
                                String.valueOf(NUMBER_OF_KVENTRY_PARTS), " parts."));
            }

            // raw fields
            String keyStr = parts[KEY_INDEX];
            String valueStr = parts[VALUE_INDEX];

            // deserialized fields
            String key = "null".equals(keyStr) ? null : keyStr;
            String value = "null".equals(valueStr) ? null : valueStr;

            // deserialized object
            deserialized = new KVEntry(key, value);
            LOGGER.debug("Deserializing a KVEntry from String finished.");
        }

        return deserialized;
    }
}
