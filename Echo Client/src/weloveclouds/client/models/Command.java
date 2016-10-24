package weloveclouds.client.models;

import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author Benedek
 */
public enum Command {
  CONNECT, DISCONNECT, SEND, LOGLEVEL, HELP, QUIT, DEFAULT;

  private static final Set<String> names = getNames();

  private static Set<String> getNames() {
    Set<String> names = new TreeSet<>();
    for (Command command : Command.values()) {
      names.add(command.name().toLowerCase());
    }
    return names;
  }

  public static Command fromString(String name) {
    return name == null || !names.contains(name) ? DEFAULT : Command.valueOf(name);
  }
}
