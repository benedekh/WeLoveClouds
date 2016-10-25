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

  public static void validateConnectArgument(String argument)
      throws IllegalArgumentException, UnknownHostException, NumberFormatException {
    if (isNullOrEmpty(argument)) {
      throw new IllegalArgumentException("Command need arguments (<IP address> and <port>)");
    } else {
      String[] argumentParts = argument.split("\\s+");
      // if we have more than two arguments, then it's wrong
      if (argumentParts.length != 2) {
        throw new IllegalArgumentException(
            "Command need arguments (<IP address> and <port>), no other arguments shall be set.");
      }
      // if it throws an exception then the host is invalid
      InetAddress.getByName(argumentParts[0]);
      // if it throws an exception then the port is not a number
      int port = Integer.parseInt(argumentParts[1]);
      if (port < 0 || port > 65536) {
        throw new IllegalArgumentException("Port should be in the range [0,65536].");
      }
    }
  }

  public static void validateSendArgument(String argument) throws IllegalArgumentException {
    if (argument == null) {
      throw new IllegalArgumentException("Message cannot be empty (null).");
    }
  }

  public static void validateLogLevelArgument(String argument) throws IllegalArgumentException {
    if (!logLevels.contains(argument)) {
      throw new IllegalArgumentException(
          "Log level is not recognized. It should be capitalized and should be one of the followings: All, DEBUG, INFO, WARN, ERROR, FATAL, OFF");
    }
  }

  public static void validateDisconnectArgument(String argument) throws IllegalArgumentException {
    if (!isNullOrEmpty(argument)) {
      throw new IllegalArgumentException("Command does not accept any argument.");
    }
  }

  public static void validateHelpArgument(String argument) {
    if (!isNullOrEmpty(argument)) {
      throw new IllegalArgumentException("Command does not accept any argument.");
    }
  }

  public static void validateQuitArgument(String argument) {
    if (!isNullOrEmpty(argument)) {
      throw new IllegalArgumentException("Command does not accept any argument.");
    }
  }

  private static boolean isNullOrEmpty(String text) {
    return text == null || text.isEmpty();
  }

}
