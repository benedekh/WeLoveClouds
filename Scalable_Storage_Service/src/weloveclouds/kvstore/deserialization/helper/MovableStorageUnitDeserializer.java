package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.kvstore.serialization.helper.MovableStorageUnitSerializer.SEPARATOR_BETWEEN_ENTRIES;
import static weloveclouds.kvstore.serialization.helper.MovableStorageUnitSerializer.SEPARATOR_INSIDE_ENTRY;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.utils.FileUtility;

/**
 * A deserializer which converts a {@link MovableStorageUnit} to a {@link String}.
 * 
 * @author Benedek
 */
public class MovableStorageUnitDeserializer implements IDeserializer<MovableStorageUnit, String> {

    private static final int NUMBER_OF_ENTRY_PARTS = 2;

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private static final Logger LOGGER = Logger.getLogger(MovableStorageUnitDeserializer.class);

    @Override
    public MovableStorageUnit deserialize(String from) throws DeserializationException {
        MovableStorageUnit deserialized = null;

        if (from != null && !"null".equals(from)) {
            // raw message split
            String[] entries = from.split(SEPARATOR_BETWEEN_ENTRIES);

            Map<String, String> deserializedEntries = new HashMap<>();
            for (String rawEntry : entries) {
                String[] rawEntryParts = rawEntry.split(SEPARATOR_INSIDE_ENTRY);

                // length check
                if (rawEntryParts.length != NUMBER_OF_ENTRY_PARTS) {
                    String errorMessage =
                            CustomStringJoiner.join("", "KVEntry must consist of exactly ",
                                    String.valueOf(NUMBER_OF_ENTRY_PARTS), " parts.");
                    LOGGER.debug(errorMessage);
                    throw new DeserializationException(errorMessage);
                }

                // raw fields
                String key = rawEntryParts[KEY_INDEX];
                String value = rawEntryParts[VALUE_INDEX];

                // deserialized fields
                deserializedEntries.put(key, value);
            }

            // deserialized object
            deserialized =
                    new MovableStorageUnit(deserializedEntries, FileUtility.createDummyPath());
        }

        return deserialized;
    }

}
