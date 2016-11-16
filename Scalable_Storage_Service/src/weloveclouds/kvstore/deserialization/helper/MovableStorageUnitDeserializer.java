package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.kvstore.serialization.helper.MovableStorageUnitSerializer.SEPARATOR_BETWEEN_ENTRIES;
import static weloveclouds.kvstore.serialization.helper.MovableStorageUnitSerializer.SEPARATOR_WITHIN_ENTRY;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.server.store.models.MovableStorageUnit;

public class MovableStorageUnitDeserializer implements IDeserializer<MovableStorageUnit, String> {

    private static final int NUMBER_OF_ENTRY_PARTS = 2;

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public MovableStorageUnit deserialize(String from) throws DeserializationException {
        MovableStorageUnit deserialized = null;

        if (from != null) {
            // raw message split
            String[] entries = from.split(SEPARATOR_BETWEEN_ENTRIES);

            Map<String, String> deserializedEntries = new HashMap<>();
            for (String rawEntry : entries) {
                String[] rawEntryParts = rawEntry.split(SEPARATOR_WITHIN_ENTRY);

                // length check
                if (rawEntryParts.length != NUMBER_OF_ENTRY_PARTS) {
                    String errorMessage =
                            CustomStringJoiner.join("", "KVEntry must consist of exactly ",
                                    String.valueOf(NUMBER_OF_ENTRY_PARTS), " parts.");
                    logger.debug(errorMessage);
                    throw new DeserializationException(errorMessage);
                }

                // raw fields
                String key = rawEntryParts[KEY_INDEX];
                String value = rawEntryParts[VALUE_INDEX];

                // deserialized fields
                deserializedEntries.put(key, value);
            }

            // deserialized object
            deserialized = new MovableStorageUnit(deserializedEntries, getDummyPath());
        }

        return deserialized;
    }

    private Path getDummyPath() {
        return new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile())
                .toPath();
    }

}
