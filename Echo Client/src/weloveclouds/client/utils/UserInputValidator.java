package weloveclouds.client.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Benedek
 */
public class UserInputValidator {

  private static List<String> logLevels =
      Arrays.asList("ALL", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");

  public static boolean isConnectArgumentValid(String argument)
      throws UnknownHostException, NumberFormatException {
    if (isNullOrEmpty(argument)) {
      return false;
    } else {
      String[] argumentParts = argument.split("\\s+");
      // if we have more than two arguments, then it's wrong
      if (argumentParts.length != 2) {
        return false;
      }
      // if it throws an exception then the host is invalid
      InetAddress.getByName(argumentParts[0]);
      // if it throws an exception then the port is not a number
      Integer.parseInt(argumentParts[1]);
      return true;
    }
  }

  public static boolean isDisconnectArgumentValid(String argument) {
    return !isNullOrEmpty(argument);
  }

  public static boolean isSendArgumentValid(String argument) {
    return argument != null;
  }

  public static boolean isLogLevelArgumentValid(String argument) {
    return logLevels.contains(argument);
  }

  public static boolean isHelpArgumentValid(String argument) {
    return !isNullOrEmpty(argument);
  }

  public static boolean isQuitArgumentValid(String argument) {
    return !isNullOrEmpty(argument);
  }

  private static boolean isNullOrEmpty(String text) {
    return text == null || text.isEmpty();
  }

}
