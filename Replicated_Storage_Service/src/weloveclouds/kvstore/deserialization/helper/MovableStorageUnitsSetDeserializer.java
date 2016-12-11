package weloveclouds.kvstore.deserialization.helper;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.MovableStorageUnitDeserializer;
import weloveclouds.kvstore.serialization.helper.MovableStorageUnitsSetSerializer;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A deserializer which converts a {@link Set<MovableStorageUnit>} to a {@link String}.
 * 
 * @author Benedek
 */
public class MovableStorageUnitsSetDeserializer
        implements IDeserializer<Set<MovableStorageUnit>, String> {

    private static final Logger LOGGER = Logger.getLogger(MovableStorageUnitsSetDeserializer.class);

    private IDeserializer<MovableStorageUnit, String> storageUnitDeserializer =
            new MovableStorageUnitDeserializer();

    @Override
    public Set<MovableStorageUnit> deserialize(String from) throws DeserializationException {
        Set<MovableStorageUnit> deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a Set<MovableStorageUnit> from String.");
            // raw message split
            String[] rawStorageUnits =
                    from.split(MovableStorageUnitsSetSerializer.SEPARATOR_BETWEEN_STORAGE_UNITS);

            // deserialized object
            deserialized = new HashSet<>();
            for (String rawStorageUnit : rawStorageUnits) {
                // deserialized fields
                deserialized.add(storageUnitDeserializer.deserialize(rawStorageUnit));
            }

            LOGGER.debug("Deserializing a Set<MovableStorageUnit> from String finished.");
        }

        return deserialized;
    }

}
