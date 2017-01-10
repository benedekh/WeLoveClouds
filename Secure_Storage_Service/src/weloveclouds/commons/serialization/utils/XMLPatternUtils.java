package weloveclouds.commons.serialization.utils;

import java.util.regex.Pattern;

import weloveclouds.commons.utils.StringUtils;

/**
 * Regular expression patterns for XML nodes.
 * 
 * @author Benoit
 */
public class XMLPatternUtils {
    public static final String XML_NODE = "group";

    /**
     * Creates a regexp to extract the content of a token.
     */
    public static Pattern getRegexFromToken(String token) {
        return Pattern.compile(createStartTokenFrom(token) + "(?<" + XML_NODE + ">.*?)"
                + createEndTokenFrom(token));
    }

    private static String createStartTokenFrom(String token) {
        return StringUtils.join("", "<", token, ">");
    }

    private static String createEndTokenFrom(String token) {
        return StringUtils.join("", "</", token, ">");
    }
}
