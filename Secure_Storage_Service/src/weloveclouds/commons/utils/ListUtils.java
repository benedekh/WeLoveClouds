package weloveclouds.commons.utils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for lists.
 * 
 * @author Benoit
 */
public class ListUtils {

    /**
     * Retrieves a random indexed element from the list (at most the highest indexed).
     */
    public static Object getRandomObjectFrom(List<?> list) {
        int randomIndex = new Random().nextInt(list.size());
        return list.get(randomIndex);
    }

    /**
     * Gets exactly the required number of elements randomly from the list.
     * 
     * @throws InvalidParameterException if the list does not contain the required number of
     *         elements.
     */
    public static List<?> getPreciseNumberOfRandomObjectsFrom(List<?> list, int numberOfObjects)
            throws InvalidParameterException {
        if (numberOfObjects > list.size()) {
            throw new InvalidParameterException(
                    "The list does not contain the requested number of object");
        }
        Random randomIndexGenerator = new Random();
        List<Object> dataSet = new ArrayList<>(list);
        List<Object> randomObjects = new ArrayList<>();

        for (int i = 0; i < numberOfObjects; i++) {
            int randomIndex = randomIndexGenerator.nextInt(dataSet.size());
            randomObjects.add(dataSet.get(randomIndex));
            dataSet.removeAll(randomObjects);
        }

        return randomObjects;
    }
}
