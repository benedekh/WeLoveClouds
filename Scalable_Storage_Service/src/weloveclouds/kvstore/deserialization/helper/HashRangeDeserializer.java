package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.helper.HashRangeSerializer;

/**
 * A deserializer which converts a {@link HashRange} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashRangeDeserializer implements IDeserializer<HashRange, String> {

    private static final int NUMBER_OF_HASH_RANGE_PARTS = 2;

    private static final int RANGE_START_INDEX = 0;
    private static final int RANGE_END_INDEX = 1;

    private static final Logger LOGGER = Logger.getLogger(HashRangeDeserializer.class);

    private IDeserializer<Hash, String> hashDeserializer = new HashDeserializer();

    @Override
    public HashRange deserialize(String from) throws DeserializationException {
        HashRange deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a HashRange from String.");
            // raw message split
            String[] parts = from.split(HashRangeSerializer.SEPARATOR);

            // length check
            if (parts.length != NUMBER_OF_HASH_RANGE_PARTS) {
                String errorMessage =
                        CustomStringJoiner.join("", "Hash range must consist of exactly ",
                                String.valueOf(NUMBER_OF_HASH_RANGE_PARTS), " parts.");
                LOGGER.debug(errorMessage);
                throw new DeserializationException(errorMessage);
            }

            // raw fields
            String startHashStr = parts[RANGE_START_INDEX];
            String endHashStr = parts[RANGE_END_INDEX];

            // deserialized fields
            Hash startHash = hashDeserializer.deserialize(startHashStr);
            Hash endHash = hashDeserializer.deserialize(endHashStr);

            // deserialized object
            deserialized = new HashRange.Builder().begin(startHash).end(endHash).build();
            LOGGER.debug(join(" ", "Deserialized hash range is:", deserialized.toString()));
        }

        return deserialized;
    }

}
