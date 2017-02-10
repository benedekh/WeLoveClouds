package weloveclouds.commons.kvstore.serialization.helper;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.serialization.ISerializer;

/**
 * A serializer which converts a {@link Hash} to a {@link String}.
 * 
 * @author Benedek, Hunton
 */
public class HashSerializer implements ISerializer<String, Hash> {

    public static final String SEPARATOR_INSIDE_HASH = "-ł-";

    @Override
    public String serialize(Hash target) {
        String serialized = null;

        if (target != null) {
            serialized = target.toStringWithDelimiter(SEPARATOR_INSIDE_HASH);
        }

        return serialized;
    }

}
