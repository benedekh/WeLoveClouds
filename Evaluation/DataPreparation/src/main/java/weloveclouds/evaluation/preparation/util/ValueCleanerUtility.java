package weloveclouds.evaluation.preparation.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ValueCleanerUtility {

    private static final int VALUE_SIZE_LIMIT = 120 * 1000;
    private static final int KEY_SIZE_LIMIT = 20;

    public static String removeIllegalCharactersFromValue(String value) {
        return value.replace("\n", "").replace("\t", "").replace("\r", "");
    }

    public static Map<String, String> cutValueIntoChunks(String startKey, String value) {
        byte[] valueBytes = value.getBytes(StandardCharsets.US_ASCII);
        Map<String, String> result = new HashMap<>();

        if (isValueSizeOverLimit(valueBytes)) {
            int id = 0;
            String key = null;
            while (isValueSizeOverLimit(valueBytes)) {
                key = StringJoinerUtility.join(File.separator, startKey, String.valueOf(id));
                if (isKeySizeOverLimit(key)) {
                    key = KeyCreatorUtility.generate20BytesKey();
                }

                byte[] chunk = new byte[VALUE_SIZE_LIMIT];
                System.arraycopy(valueBytes, 0, chunk, 0, VALUE_SIZE_LIMIT);
                byte[] remainingValueBytes = new byte[valueBytes.length - chunk.length];
                System.arraycopy(valueBytes, VALUE_SIZE_LIMIT, remainingValueBytes, 0,
                        remainingValueBytes.length);
                valueBytes = remainingValueBytes;

                result.put(key, new String(chunk, StandardCharsets.US_ASCII));
                id++;
            }
            result.put(key, new String(valueBytes, StandardCharsets.US_ASCII));

        } else {
            result.put(startKey, value);
        }

        return result;
    }

    private static boolean isKeySizeOverLimit(String key) {
        return key.getBytes(StandardCharsets.US_ASCII).length > KEY_SIZE_LIMIT;
    }

    private static boolean isValueSizeOverLimit(byte[] valueAsArray) {
        return valueAsArray.length > VALUE_SIZE_LIMIT;
    }
}
