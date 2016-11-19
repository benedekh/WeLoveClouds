package weloveclouds.kvstore.serialization.helper;

import weloveclouds.hashing.models.Hash;

/**
 * A serializer which converts a {@link Hash} to a {@link String}.
 * 
 * @author Benedek
 */
public class HashSerializer implements ISerializer<String, Hash> {

    public static final String SEPARATOR_INSIDE_HASH = "-\t-";

    @Override
    public String serialize(Hash target) {
        String serialized = null;

        if (target != null) {
            serialized = target.toStringWithDelimiter(SEPARATOR_INSIDE_HASH);
        }

        return serialized;
    }

}
