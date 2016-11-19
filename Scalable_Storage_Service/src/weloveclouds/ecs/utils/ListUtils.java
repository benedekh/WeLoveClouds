package weloveclouds.ecs.utils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Benoit on 2016-11-19.
 */
public class ListUtils {
    public static Object getRandomObjectFrom(List<?> list) {
        int randomIndex = new Random().nextInt(list.size());
        return list.get(randomIndex);
    }

    public static List<?> getPreciseNumberOfRandomObjectsFrom(List<?> list, int
            numberOfObjects)
            throws InvalidParameterException {
        if (numberOfObjects > list.size()) {
            throw new InvalidParameterException("The list does not contain the requested number " +
                    "of object");
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
