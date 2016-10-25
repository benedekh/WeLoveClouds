package weloveclouds.client.models;

import java.util.Set;
import java.util.TreeSet;

/**
 * The commmand enum is used to represent user commands within the program.
 * @author Benedek
 */
public enum Command {
  CONNECT, DISCONNECT, SEND, LOGLEVEL, HELP, QUIT, DEFAULT;

  private static final Set<String> names = getNames();

  /**
   * getNames gets the set of commands as strings.
   * @return - The commands as strings.
   */
  private static Set<String> getNames() {
    Set<String> names = new TreeSet<>();
    for (Command command : Command.values()) {
      names.add(command.name().toLowerCase());
    }
    return names;
  }

  /**
   * Gets the enum value of a command in string form.
   * @param name - the string of a command.
   * @return - the enum of a command.
   */
  public static Command fromString(String name) {
    return name == null || !names.contains(name) ? DEFAULT : Command.valueOf(name);
  }
}
