package weloveclouds.commons.utils;

import java.util.HashSet;
import java.util.Set;

public class SetUtils {
    
    public static <T> Set<T> removeValueFromSet(Set<T> values, T value){
        HashSet<T> copy = new HashSet<>(values);
        copy.remove(value);
        return copy;
    }

}
