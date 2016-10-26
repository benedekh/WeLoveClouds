package weloveclouds.client.models;

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.StringJoiner;

/**
 * Different commands which are handled by
 * {@link weloveclouds.client.models.commands.CommandFactory}.
 * 
 * @author Benedek
 */
public enum Command {
    CONNECT, DISCONNECT, SEND, LOGLEVEL, HELP, QUIT, DEFAULT;

    private static final Logger LOGGER = Logger.getLogger(Command.class);

    private static final Set<String> lowercaseCommands = getCommandsAsLowercase();

    /**
     * Creates a set from the lower-case names of the commands.
     */
    private static Set<String> getCommandsAsLowercase() {
        Set<String> names = new TreeSet<>();
        for (Command command : Command.values()) {
            names.add(command.name().toLowerCase());
        }
        return names;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static Command fromString(String command) {
        Command recognized = (command == null || !lowercaseCommands.contains(command) ? DEFAULT
                : Command.valueOf(command));
        if (recognized == DEFAULT) {
            LOGGER.warn(StringJoiner.join("", "Command (", command, ") is not recognized."));
        }
        return recognized;
    }

}
