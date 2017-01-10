package weloveclouds.evaluation.preparation.util;

import java.util.StringJoiner;

/**
 * The type String joiner utility.
 */
public class StringJoinerUtility {

    /**
     * Join string.
     *
     * @param separator the separator
     * @param parts     the parts
     * @return the string
     */
    public static String join(final String separator, final String... parts) {
        StringJoiner joiner = new StringJoiner(separator);
        for (String part : parts) {
            joiner.add(part.trim());
        }
        return joiner.toString();
    }

}
