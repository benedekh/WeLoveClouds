package weloveclouds.server.utils;

import java.util.Set;

/**
 * Helper class for Set to String operations.
 * 
 * @author Benedek
 */
public class SetToStringUtility {

    /**
     * Converts every element of the Set into a String by calling their toString methods and
     * concatenating the String representations with a delimiter (, ).
     */
    public static String toString(Set<?> set) {
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
