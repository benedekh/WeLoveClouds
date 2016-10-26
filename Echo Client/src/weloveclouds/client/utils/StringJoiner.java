package weloveclouds.client.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Joins several strings together efficiently.
 * 
 * @author Benedek
 */
public abstract class StringJoiner {

    /**
     * Joins string fragments using the delimiter string into one string.
     */
    public static String join(String delimiter, String... fragments) {
        return join(delimiter, Arrays.asList(fragments));
    }

    /**
     * Joins string fragments using the delimiter string into one string.
     */
    public static String join(String delimiter, List<String> fragments) {
        if (fragments != null && fragments.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (String fragment : fragments) {
                buffer.append(fragment);
                buffer.append(delimiter);
            }
            buffer.setLength(buffer.length() - delimiter.length());
            return buffer.toString();
        } else {
            return "";
        }
    }
}
