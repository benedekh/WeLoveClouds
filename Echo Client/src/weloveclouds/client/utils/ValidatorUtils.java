package weloveclouds.client.utils;

/**
 * Created by Benoit on 2016-10-25.
 */
public class ValidatorUtils {
    public static boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static boolean isNullOrEmpty(String[] arguments) {
        return arguments == null || arguments.length == 0;
    }

    public static boolean isNullOrEmpty(byte[] message) {
        return message == null || message.length == 0;
    }
}
