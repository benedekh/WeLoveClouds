package weloveclouds.evaluation.preparation.util;

import java.util.StringJoiner;

public class StringJoinerUtility {

    public static String join(final String separator, final String... parts) {
        StringJoiner joiner = new StringJoiner(separator);
        for (String part : parts) {
            joiner.add(part.trim());
        }
        return joiner.toString();
    }

}
