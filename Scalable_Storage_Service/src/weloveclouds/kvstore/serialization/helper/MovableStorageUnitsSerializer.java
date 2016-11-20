package weloveclouds.kvstore.serialization.helper;

import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;

public class MovableStorageUnitsSerializer implements ISerializer<String, MovableStorageUnits> {

    public static final String SEPARATOR_BETWEEN_STORAGE_UNITS = "-\r-";

    private ISerializer<String, MovableStorageUnit> storageUnitSerializer =
            new MovableStorageUnitSerializer();

    @Override
    public String serialize(MovableStorageUnits target) {
        String serialized = null;

        if (target != null) {
            serialized = target.toStringWithDelimiter(SEPARATOR_BETWEEN_STORAGE_UNITS,
                    storageUnitSerializer);
        }

        return serialized;
    }

}
