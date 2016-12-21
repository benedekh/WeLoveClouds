package weloveclouds.communication.utils;

import weloveclouds.commons.utils.StringUtils;

public class RegexpFactory {

    /**
     * Creates the following regexp: "<tag>.*?</tag>" where tag is the parameter of this method.
     */
    public static String createRegexpForTag(String tag) {
        return StringUtils.join(".*?", createOpeningTag(tag), createClosingTag(tag));
    }

    private static String createOpeningTag(String tag) {
        return StringUtils.join("", "<", tag, ">");
    }

    private static String createClosingTag(String tag) {
        return StringUtils.join("", "</", tag, ">");
    }

}
