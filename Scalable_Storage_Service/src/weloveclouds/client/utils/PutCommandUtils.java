package weloveclouds.client.utils;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for the Put command
 * 
 * @author Benedek
 */
public class PutCommandUtils {

    /**
     * Merges string arguments into one string, separated by a space, starting from the respective
     * startIndex until the and of the array.
     */
    public static String mergeValuesToOneString(int startIndex, String[] arguments) {
        List<String> argList = Arrays.asList(arguments);
        List<String> valueElements = argList.subList(startIndex, argList.size());
        return join(" ", valueElements);
    }

}
