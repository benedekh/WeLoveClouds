package weloveclouds.server.utils;

import java.util.Set;

public class SetToStringUtility {

    public static String toString(Set<?> set) {
        if (set != null) {
            String delimiter = ", ";

            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (Object object : set) {
                sb.append(object);
                sb.append(delimiter);
            }
            sb.setLength(sb.length() - delimiter.length());
            sb.append("}");

            return sb.toString();
        } else {
            return null;
        }
    }

}
