package weloveclouds.client.models;

import java.nio.charset.StandardCharsets;

/**
 * @author Benoit, Benedek
 */
public class UserInput {
  private String command;
  private String[] arguments = {};

  protected UserInput(UserInputFactory factory) {
    this.command = factory.command;
    this.arguments = factory.argument;
  }

  public Command getCommand() {
    return Command.fromString(command);
  }

  public String[] getArguments() {
    return arguments;
  }

  public static class UserInputFactory {
    private String command;
    private String[] argument;

    public UserInputFactory command(String command) {
      this.command = command;
      return this;
    }

    public UserInputFactory arguments(String[] argument) {
      this.argument = argument;
      return this;
    }

    public UserInput build() {
      return new UserInput(this);
    }
  }
}
