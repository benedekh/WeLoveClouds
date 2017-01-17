package weloveclouds.commons.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for Sets.
 * 
 * @author Benedek
 */
public class SetUtils {

    /**
     * Returns a set which is the copy of the original set, but it does not contain the referred
     * value.
     */
    public static <T> Set<T> removeValueFromSet(Set<T> values, T value) {
        HashSet<T> copy = new HashSet<>(values);
        copy.remove(value);
        return copy;
    }
}
