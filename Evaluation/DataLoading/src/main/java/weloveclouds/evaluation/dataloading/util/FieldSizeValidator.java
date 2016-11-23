package weloveclouds.evaluation.dataloading.util;

import java.nio.charset.StandardCharsets;

public class FieldSizeValidator {

    private static final int KEY_SIZE_LIMIT = 20;
    private static final int VALUE_SIZE_LIMIT = 120 * 1000;

    public static boolean isKeySizeOverLimit(final String key) {
        return isStringOverLimit(key, KEY_SIZE_LIMIT);
    }

    public static boolean isValueSizeOverLimit(final String value) {
        return isStringOverLimit(value, VALUE_SIZE_LIMIT);
    }

    private static boolean isStringOverLimit(final String string, final int limit) {
        return string.getBytes(StandardCharsets.UTF_8).length > limit;
    }
}
