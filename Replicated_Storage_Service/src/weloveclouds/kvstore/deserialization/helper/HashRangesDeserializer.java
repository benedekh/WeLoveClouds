package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.HashRanges;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashRangesSerializer;

/**
 * A deserializer which converts a {@link HashRanges} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashRangesDeserializer implements IDeserializer<HashRanges, String> {

    private static final Logger LOGGER = Logger.getLogger(HashRangesDeserializer.class);

    private IDeserializer<HashRange, String> hashRangeDeserializer = new HashRangeDeserializer();

    @Override
    public HashRanges deserialize(String from) throws DeserializationException {
        HashRanges deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a HashRanges from String.");
            // raw message split
            String[] parts = from.split(HashRangesSerializer.SEPARATOR);

            // deserialized fields
            Set<HashRange> ranges = new HashSet<>();
            for (String serializedRange : parts) {
                ranges.add(hashRangeDeserializer.deserialize(serializedRange));
            }

            // deserialized object
            deserialized = new HashRanges(ranges);
            LOGGER.debug(join(" ", "Deserialized HashRanges is:", deserialized.toString()));
        }

        return deserialized;
    }

}
