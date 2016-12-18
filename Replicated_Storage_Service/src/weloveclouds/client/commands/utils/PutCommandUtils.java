package weloveclouds.client.commands.utils;

import java.util.Arrays;
import java.util.List;

import weloveclouds.commons.utils.StringUtils;

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
        String mergedValues = StringUtils.join(" ", valueElements);

        // to preserve the really empty entry value
        if (mergedValues != null && mergedValues.trim().isEmpty()) {
            mergedValues = null;
        }

        return mergedValues;
    }

}
