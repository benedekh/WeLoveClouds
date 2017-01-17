package weloveclouds.commons.utils;

import java.util.Arrays;
import java.util.Collection;
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
    @SafeVarargs
    public static <T> String join(String delimiter, T... fragments) {
        return join(delimiter, Arrays.asList(fragments));
    }

    /**
     * Joins fragments (by calling their {@link #toString()} method) using the delimiter string into
     * one string.
     */
    public static <T> String join(String delimiter, Collection<T> fragments) {
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

    /**
     * @return true if the text is not null and it is not empty either.
     */
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
            int lengthAfterRemovingTheLastDelimiter = sb.length() - delimiter.length();
            if (lengthAfterRemovingTheLastDelimiter > 0) {
                sb.setLength(lengthAfterRemovingTheLastDelimiter);
            }
            sb.append("}");

            return sb.toString();
        } else {
            return null;
        }
    }
}
