package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * A serializer which converts a {@link MovableStorageUnits} to a {@link String}.
 * 
 * @author Benedek
 */
public class MovableStorageUnitsSerializer implements ISerializer<String, MovableStorageUnits> {

    public static final String SEPARATOR_BETWEEN_STORAGE_UNITS = "-Ł-";
    private static final Logger LOGGER = Logger.getLogger(MovableStorageUnitsSerializer.class);
    
    private ISerializer<String, MovableStorageUnit> storageUnitSerializer =
            new MovableStorageUnitSerializer();

    @Override
    public String serialize(MovableStorageUnits target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a MovableStorageUnits.");
            serialized = target.toStringWithDelimiter(SEPARATOR_BETWEEN_STORAGE_UNITS,
                    storageUnitSerializer);
            LOGGER.debug("Serializing a MovableStorageUnits finished.");
        }

        return serialized;
    }

}
