package weloveclouds.kvstore.serialization.helper;

import weloveclouds.server.store.models.MovableStorageUnits;

public class MovableStorageUnitsSerializer implements ISerializer<String, MovableStorageUnits> {

    public static final String SEPARATOR_BETWEEN_STORAGE_UNITS = "-\r-";

    @Override
    public String serialize(MovableStorageUnits target) {
        String serialized = null;

        if (target != null) {
            serialized = target.toStringWithDelimiter(SEPARATOR_BETWEEN_STORAGE_UNITS,
                    MovableStorageUnitSerializer.SEPARATOR_BETWEEN_ENTRIES,
                    MovableStorageUnitSerializer.SEPARATOR_WITHIN_ENTRY);
        }

        return serialized;
    }

}
