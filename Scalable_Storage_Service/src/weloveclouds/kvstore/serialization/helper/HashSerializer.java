package weloveclouds.kvstore.serialization.helper;

import weloveclouds.hashing.models.Hash;

public class HashSerializer implements ISerializer<String, Hash> {

    public static final String SEPARATOR = "|";

    @Override
    public String serialize(Hash target) {
        String serialized = null;

        if (target != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : target.getHash()) {
                sb.append(String.valueOf(b));
                sb.append(SEPARATOR);
            }
            sb.setLength(sb.length() - SEPARATOR.length());
            serialized = sb.toString();
        }

        return serialized;
    }

}
