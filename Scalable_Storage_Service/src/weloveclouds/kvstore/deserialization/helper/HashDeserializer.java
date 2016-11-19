package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.Hash;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.HashSerializer;

/**
 * A deserializer which converts a {@link Hash} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashDeserializer implements IDeserializer<Hash, String> {

    private static final int NUMBER_OF_HASH_PARTS = 16;
    private static final Logger LOGGER = Logger.getLogger(HashDeserializer.class);

    @Override
    public Hash deserialize(String from) throws DeserializationException {
        Hash deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a Hash from String.");
            // raw message split
            String[] parts = from.split(HashSerializer.SEPARATOR_INSIDE_HASH);

            // length check
            if (parts.length != NUMBER_OF_HASH_PARTS) {
                String errorMessage = CustomStringJoiner.join("", "Hash must consist of exactly ",
                        String.valueOf(NUMBER_OF_HASH_PARTS), " parts.");
                LOGGER.debug(errorMessage);
                throw new DeserializationException(errorMessage);
            }

            int i = 0;
            try {
                // deserialized fields
                byte[] hash = new byte[NUMBER_OF_HASH_PARTS];
                for (i = 0; i < NUMBER_OF_HASH_PARTS; ++i) {
                    hash[i] = Byte.valueOf(parts[i]);
                }

                // deserialized object
                deserialized = new Hash(hash);
                LOGGER.debug(join(" ", "Deserialized hash is:", deserialized.toString()));
            } catch (NumberFormatException ex) {
                String errorMessage =
                        CustomStringJoiner.join("", "Deserialized hash byte segment at index ",
                                String.valueOf(i), " is not a byte: ", parts[i]);
                LOGGER.debug(errorMessage);
                throw new DeserializationException(errorMessage);
            }
        }
        
        return deserialized;
    }

}
