package weloveclouds.kvstore.serialization.helper;

import org.apache.log4j.Logger;

import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A serializer which converts a {@link MovableStorageUnit} to a {@link String}.
 * 
 * @author Benedek
 */
public class MovableStorageUnitSerializer implements ISerializer<String, MovableStorageUnit> {

    public static final String SEPARATOR_BETWEEN_ENTRIES = "-\t-";
    public static final String SEPARATOR_INSIDE_ENTRY = "::\r::";
    private static final Logger LOGGER = Logger.getLogger(MovableStorageUnitSerializer.class);

    @Override
    public String serialize(MovableStorageUnit target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a MovableStorageUnit.");
            serialized =
                    target.toStringWithDelimiter(SEPARATOR_BETWEEN_ENTRIES, SEPARATOR_INSIDE_ENTRY);
            LOGGER.debug("Serializing a MovableStorageUnit finished.");
        }

        return serialized;
    }

}
