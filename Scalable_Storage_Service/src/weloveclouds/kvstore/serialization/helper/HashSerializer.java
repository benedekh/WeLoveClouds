package weloveclouds.kvstore.serialization.helper;

import weloveclouds.hashing.models.Hash;

public class HashSerializer implements ISerializer<String, Hash> {

    public static final String SEPARATOR = "|";

    @Override
    public String serialize(Hash target) {
        String serialized = null;

        if (target != null) {
            serialized = target.toStringWithDelimiter(SEPARATOR);
        }

        return serialized;
    }

}
