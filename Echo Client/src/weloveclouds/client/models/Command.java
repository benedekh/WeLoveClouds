package weloveclouds.client.models;

import java.util.Set;
import java.util.TreeSet;

public enum Command {
  CONNECT, DISCONNECT, SEND, LOGLEVEL, HELP, QUIT, DEFAULT;

  private static final Set<String> names = getNames();

  private static Set<String> getNames() {
    Set<String> names = new TreeSet<>();
    for (Command command : Command.values()) {
      names.add(command.name().toUpperCase());
    }
    return names;
  }

  public static Command fromString(String name) {
    if (name == null || !names.contains(name.toUpperCase())) {
      return DEFAULT;
    } else {
      return Command.valueOf(name.toUpperCase());
    }
  }
}
