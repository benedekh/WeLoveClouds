package weloveclouds.kvstore.deserialization.helper;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.hashing.models.HashRange;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashRangesSetSerializer;

/**
 * A deserializer which converts a {@link Set<HashRange>} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashRangesSetDeserializer implements IDeserializer<Set<HashRange>, String> {

    private static final Logger LOGGER = Logger.getLogger(HashRangesSetDeserializer.class);

    private IDeserializer<HashRange, String> hashRangeDeserializer = new HashRangeDeserializer();

    @Override
    public Set<HashRange> deserialize(String from) throws DeserializationException {
        Set<HashRange> deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a Set<HashRange> from String.");
            // raw message split
            String[] parts = from.split(HashRangesSetSerializer.SEPARATOR);

            // deserialized object
            deserialized = new HashSet<>();
            for (String serializedRange : parts) {
                deserialized.add(hashRangeDeserializer.deserialize(serializedRange));
            }

            LOGGER.debug("Deserializing a Set<HashRange> from String finished.");
        }

        return deserialized;
    }

}
