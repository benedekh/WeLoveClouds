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
 * @author Benoit
 */
public class UserInputParser {
  private static final Pattern INPUT_REGEX =
      Pattern.compile("(?<command>\\w+) " + "(?<argument>[a-zA-Z0-9 ]+)");

  public static UserInput parse(String userInput) {
    String workingString = userInput.trim();
    Matcher matcher = INPUT_REGEX.matcher(workingString);
    matcher.find();
    return new UserInput.UserInputFactory().command(matcher.group("command"))
        .arguments(matcher.group("argument")).build();
  }

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
