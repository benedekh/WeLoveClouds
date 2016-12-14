package weloveclouds.commons.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Joins several strings together efficiently.
 *
 * @author Benedek
 */
public class StringUtils {

    /**
     * Joins fragments (by calling their {@link #toString()} method) using the delimiter string into
     * one string.
     */
    public static String join(String delimiter, Object... fragments) {
        return join(delimiter, Arrays.asList(fragments));
    }

    /**
     * Joins fragments (by calling their {@link #toString()} method) using the delimiter string into
     * one string.
     */
    public static String join(String delimiter, List<Object> fragments) {
        if (fragments != null && fragments.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (Object fragment : fragments) {
                buffer.append(fragment);
                buffer.append(delimiter);
            }
            buffer.setLength(buffer.length() - delimiter.length());
            return buffer.toString();
        } else {
            return "";
        }
    }

    public static boolean stringIsNotEmpty(String text) {
        return text != null && !"null".equals(text) && !text.isEmpty();
    }

    /**
     * Converts every element of the Set into a String by calling their toString methods and
     * concatenating the String representations with a delimiter (, ).
     */
    public static String setToString(Set<?> set) {
        if (set != null) {
            String delimiter = ", ";

            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (Object object : set) {
                sb.append(object);
                sb.append(delimiter);
            }
            int lengthAfterRemovingTheLastDelmiter = sb.length() - delimiter.length();
            if (lengthAfterRemovingTheLastDelmiter > 0) {
                sb.setLength(lengthAfterRemovingTheLastDelmiter);
            }
            sb.append("}");

            return sb.toString();
        } else {
            return null;
        }
    }
}