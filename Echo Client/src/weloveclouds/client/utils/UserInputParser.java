package weloveclouds.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weloveclouds.client.models.UserInput;

/**
 * @author Benoit
 */
public class UserInputParser {
  private static final Pattern INPUT_REGEX =
      Pattern.compile("(?<command>\\w+) " + "(?<argument>[a-zA-Z0-9 ]+)");

  public static UserInput parse(String userInput) {
    Matcher matcher = INPUT_REGEX.matcher(userInput);
    matcher.find();
    return new UserInput.UserInputFactory().command(matcher.group("command"))
        .arguments(matcher.group("argument")).build();
  }
}
