package weloveclouds.evaluation.preparation.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Value cleaner utility.
 * 
 * @author Benedek
 */
public class ValueCleanerUtility {

    private static final int VALUE_SIZE_LIMIT = 120 * 1000;

    /**
     * Remove illegal characters from value string.
     *
     * @param value the value
     * @return the string
     */
    public static String removeIllegalCharactersFromValue(String value) {
        return value.replace("\n", "").replace("\t", "").replace("\r", "").replace("\\", "");
    }

    /**
     * Cut value into chunks map.
     *
     * @param value the value
     * @return the map
     */
    public static Map<String, String> cutValueIntoChunks(final String value) {
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
        Map<String, String> result = new HashMap<>();

        if (isValueSizeOverLimit(valueBytes)) {
            while (isValueSizeOverLimit(valueBytes)) {
                byte[] chunk = new byte[VALUE_SIZE_LIMIT];
                System.arraycopy(valueBytes, 0, chunk, 0, VALUE_SIZE_LIMIT);
                byte[] remainingValueBytes = new byte[valueBytes.length - chunk.length];
                System.arraycopy(valueBytes, VALUE_SIZE_LIMIT, remainingValueBytes, 0,
                        remainingValueBytes.length);
                valueBytes = remainingValueBytes;

                String key = KeyCreatorUtility.generate20BytesKey();
                result.put(key, new String(chunk, StandardCharsets.UTF_8));
            }
            String key = KeyCreatorUtility.generate20BytesKey();
            result.put(key, new String(valueBytes, StandardCharsets.UTF_8));

        } else {
            String key = KeyCreatorUtility.generate20BytesKey();
            result.put(key, value);
        }

        return result;
    }

    private static boolean isValueSizeOverLimit(final byte[] valueAsArray) {
        return valueAsArray.length > VALUE_SIZE_LIMIT;
    }
}
