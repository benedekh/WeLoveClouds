package weloveclouds.kvstore.serialization.helper;

import weloveclouds.server.store.models.MovableStorageUnit;

public class MovableStorageUnitSerializer implements ISerializer<String, MovableStorageUnit> {

    public static final String SEPARATOR_BETWEEN_ENTRIES = "-\t-";
    public static final String SEPARATOR_WITHIN_ENTRY = "::\r::";

    @Override
    public String serialize(MovableStorageUnit target) {
        String serialized = null;

        if (target != null) {
            serialized =
                    target.toStringWithDelimiter(SEPARATOR_BETWEEN_ENTRIES, SEPARATOR_WITHIN_ENTRY);
        }

        return serialized;
    }

}
