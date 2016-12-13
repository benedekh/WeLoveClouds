package weloveclouds.commons.utils;

public class StringUtils {

    public static boolean stringIsNotEmpty(String text) {
        return text != null && !"null".equals(text) && !text.isEmpty();
    }

}
