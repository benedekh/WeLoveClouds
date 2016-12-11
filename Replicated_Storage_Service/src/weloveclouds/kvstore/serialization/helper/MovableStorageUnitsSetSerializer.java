package weloveclouds.kvstore.serialization.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.commons.kvstore.serialization.helper.MovableStorageUnitSerializer;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A serializer which converts a {@link Set<MovableStorageUnit>} to a {@link String}.
 * 
 * @author Benedek
 */
public class MovableStorageUnitsSetSerializer
        implements ISerializer<String, Set<MovableStorageUnit>> {

    public static final String SEPARATOR_BETWEEN_STORAGE_UNITS = "-Ł-";
    private static final Logger LOGGER = Logger.getLogger(MovableStorageUnitsSetSerializer.class);

    private ISerializer<String, MovableStorageUnit> storageUnitSerializer =
            new MovableStorageUnitSerializer();

    @Override
    public String serialize(Set<MovableStorageUnit> target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a Set<MovableStorageUnit>.");
            // string representation
            Set<String> storageUnitsStrs = new HashSet<>();
            for (MovableStorageUnit storageUnit : target) {
                storageUnitsStrs.add(storageUnitSerializer.serialize(storageUnit));
            }

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR_BETWEEN_STORAGE_UNITS,
                    new ArrayList<>(storageUnitsStrs));
            LOGGER.debug("Serializing a Set<MovableStorageUnit> finished.");
        }

        return serialized;
    }

}
