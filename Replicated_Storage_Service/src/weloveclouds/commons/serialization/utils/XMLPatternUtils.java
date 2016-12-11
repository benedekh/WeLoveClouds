package weloveclouds.commons.serialization.utils;


import java.util.regex.Pattern;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Created by Benoit on 2016-12-10.
 */
public class XMLPatternUtils {
    public static final String XML_NODE = "group";

    public static Pattern getRegexFromToken(String token) {
        return Pattern.compile(createStartTokenFrom(token) + "(?<" + XML_NODE + ">.+?)"
                + createEndTokenFrom(token));
    }

    private static String createStartTokenFrom(String token) {
        return CustomStringJoiner.join("", "<", token, ">");
    }

    private static String createEndTokenFrom(String token) {
        return CustomStringJoiner.join("", "</", token, ">");
    }
}
