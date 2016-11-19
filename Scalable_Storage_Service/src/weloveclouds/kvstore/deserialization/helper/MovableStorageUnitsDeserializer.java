package weloveclouds.kvstore.deserialization.helper;

import java.util.HashSet;
import java.util.Set;

import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.MovableStorageUnitsSerializer;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * A deserializer which converts a {@link MovableStorageUnits} to a {@link String}.
 * 
 * @author Benedek
 */
public class MovableStorageUnitsDeserializer implements IDeserializer<MovableStorageUnits, String> {

    private IDeserializer<MovableStorageUnit, String> storageUnitDeserializer =
            new MovableStorageUnitDeserializer();

    @Override
    public MovableStorageUnits deserialize(String from) throws DeserializationException {
        MovableStorageUnits deserialized = null;
        Set<MovableStorageUnit> deserializedUnits = new HashSet<>();

        if (from != null && !"null".equals(from)) {
            // raw message split
            String[] rawStorageUnits =
                    from.split(MovableStorageUnitsSerializer.SEPARATOR_BETWEEN_STORAGE_UNITS);

            // raw fields
            for (String rawStorageUnit : rawStorageUnits) {
                // deserialized fields
                deserializedUnits.add(storageUnitDeserializer.deserialize(rawStorageUnit));
            }

            // deserialized object
            deserialized = new MovableStorageUnits(deserializedUnits);
        }

        return deserialized;
    }

}
