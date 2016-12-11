package weloveclouds.commons.kvstore.serialization.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
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
            // original fields
            Set<MovableStorageUnit> storageUnits = target.getStorageUnits();

            // string representation
            Set<String> storageUnitsStrs = new HashSet<>();
            for (MovableStorageUnit storageUnit : storageUnits) {
                storageUnitsStrs.add(storageUnitSerializer.serialize(storageUnit));
            }

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR_BETWEEN_STORAGE_UNITS,
                    new ArrayList<>(storageUnitsStrs));
            LOGGER.debug("Serializing a MovableStorageUnits finished.");
        }

        return serialized;
    }

}
