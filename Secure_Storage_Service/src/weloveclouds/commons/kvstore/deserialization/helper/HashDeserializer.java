package weloveclouds.commons.kvstore.deserialization.helper;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.helper.HashSerializer;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;

/**
 * A deserializer which converts a {@link String} to a {@link Hash}.
 * 
 * @author Benedek, Hunton
 */
public class HashDeserializer implements IDeserializer<Hash, String> {

    private static final int NUMBER_OF_HASH_PARTS = 16;

    @Override
    public Hash deserialize(String from) throws DeserializationException {
        Hash deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            // raw message split
            String[] parts = from.split(HashSerializer.SEPARATOR_INSIDE_HASH);

            // length check
            if (parts.length != NUMBER_OF_HASH_PARTS) {
                throw new DeserializationException(StringUtils.join("",
                        "Hash must consist of exactly ", NUMBER_OF_HASH_PARTS, " parts."));
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
            } catch (NumberFormatException ex) {
                throw new DeserializationException(
                        StringUtils.join("", "Deserialized hash byte segment at index ",
                                String.valueOf(i), " is not a byte: ", parts[i]));
            }
        }

        return deserialized;
    }

}
