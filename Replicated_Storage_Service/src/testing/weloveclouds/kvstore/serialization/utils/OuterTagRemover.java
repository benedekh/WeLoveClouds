package testing.weloveclouds.kvstore.serialization.utils;

import java.util.regex.Matcher;

import weloveclouds.commons.serialization.utils.XMLPatternUtils;

public class OuterTagRemover {

    public static String removeOuterTag(String from, String tag) {
        Matcher matcher = XMLPatternUtils.getRegexFromToken(tag).matcher(from);
        matcher.find();
        return matcher.group(XMLPatternUtils.XML_NODE);
    }

    
}
