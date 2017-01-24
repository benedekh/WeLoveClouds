package weloveclouds.evaluation.dataloading.util;

import java.nio.charset.StandardCharsets;

/**
 * The type Field size validator.
 * 
 * @author Benedek
 */
public class FieldSizeValidator {

    private static final int KEY_SIZE_LIMIT = 20;
    private static final int VALUE_SIZE_LIMIT = 120 * 1000;

    /**
     * Is key size over limit boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public static boolean isKeySizeOverLimit(final String key) {
        return isStringOverLimit(key, KEY_SIZE_LIMIT);
    }

    /**
     * Is value size over limit boolean.
     *
     * @param value the value
     * @return the boolean
     */
    public static boolean isValueSizeOverLimit(final String value) {
        return isStringOverLimit(value, VALUE_SIZE_LIMIT);
    }

    private static boolean isStringOverLimit(final String string, final int limit) {
        return string.getBytes(StandardCharsets.UTF_8).length > limit;
    }
}
