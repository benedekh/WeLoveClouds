package weloveclouds.client.utils;

import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;

import weloveclouds.client.models.UserInput;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * The UserInputParser class is used for interpreting user input. 
 * @author Benoit
 */
public class UserInputParser {
  private static final Pattern INPUT_REGEX =
      Pattern.compile("(?<command>\\w+) " + "(?<argument>[a-zA-Z0-9 ]+)");

  /**
   * parse, uses the regex to process an input string.
   * @param userInput - A string from the input stream.
   * @return - A UserInput object containing the information of the input, 
   *    @see weloveclouds.client.models.UserInput
   */
  public static UserInput parse(String userInput) {
    String workingString = userInput.trim();
    Matcher matcher = INPUT_REGEX.matcher(workingString);
    matcher.find();
    return new UserInput.UserInputFactory().command(matcher.group("command"))
        .arguments(matcher.group("argument")).build();
  }

  /**
   * extractConnectionInfoFromInput, does as the name says.
   * @param userInput - String from the input stream.
   * @return - a ServerConnectionInfo object,
   *    @see weloveclouds.communication.models
   */
  public static ServerConnectionInfo extractConnectionInfoFromInput(String userInput) {
    String[] argumentParts = userInput.split("\\s+");
    String ip = argumentParts[0];
    int port = Integer.parseInt(argumentParts[1]);
    try {
      return new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress(ip).port(port)
          .build();
    } catch (UnknownHostException e) {
      return null;
    }
  }
}
