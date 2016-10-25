package weloveclouds.client.models;

/**
 * @author Benoit, Benedek
 */
public class ParsedUserInput {
  private String command;
  private String[] arguments = {};

  protected ParsedUserInput(UserInputFactory factory) {
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

    public ParsedUserInput build() {
      return new ParsedUserInput(this);
    }
  }
}
