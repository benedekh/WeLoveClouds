package weloveclouds.client.models;

import java.nio.charset.StandardCharsets;

/**
 * @author Benoit, Benedek
 * The UserInput class is used to contain user commands and arguments, and to convert them
 * to byte format.
 */
public class UserInput {
  private String command;
  private String argument;
  
  /**
   * UserInput constructor.
   * @param factory - UserInputFactory, used to build new UserInput objects.
   */
  protected UserInput(UserInputFactory factory) {
    this.command = factory.command;
    this.argument = factory.argument;
  }
  /**
   * getCommand returns the command contained herein
   * @return - A command represented by a member of the command enum.
   * @see weloveclouds.client.models.Command
   */
  public Command getCommand() {
    return Command.fromString(command);
  }

  /**
   * getArgument returns the argument contained herein.
   * @return - The argument string.
   */
  public String getArgument() {
    return argument;
  }

  /**
   * getArgumentAsBytes returns the byte representation of the bytes contained herein.
   * @return - A byte array representing the argument.
   * @see java.lang.String.getBytes(Charset charset)
   */
  public byte[] getArgumentAsBytes() {
    if (argument == null) {
      return null;
    } else {
      return argument.getBytes(StandardCharsets.US_ASCII);
    }
  }

  /**
   * The UserInputFactory class is used to instantiate UserInput objects
   * @author Benedek, Benoit, hb
   */
  public static class UserInputFactory {
    private String command;
    private String argument;

    /**
     * command sets the internal command string of the UserInputFactory.
     * @param command - A string, ideally the string form of a command.
     * @see weloveclouds.client.models.Command
     * @return - The UserInputFactory object.
     */
    public UserInputFactory command(String command) {
      this.command = command;
      return this;
    }

    /**
     * arguments sets the internal argument string of the UserInputFactory.
     * @param argument - A string, representing the argument
     * @return - The UserInputFactory object.
     */
    public UserInputFactory arguments(String argument) {
      this.argument = argument;
      return this;
    }
    
    /**
     * build is used to build new UserInput objects.
     * @return - The new UserInput object.
     */
    public UserInput build() {
      return new UserInput(this);
    }
  }
}
