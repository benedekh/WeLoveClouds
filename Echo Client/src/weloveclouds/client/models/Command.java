package weloveclouds.client.models;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Different commands which are handled by
 * {@link weloveclouds.client.models.commands.CommandFactory}.
 * 
 * @author Benedek
 */
public enum Command {
    CONNECT("connect"), DISCONNECT("disconnect"), SEND("send"), LOGLEVEL("logLevel"), HELP(
            "help"), QUIT("quit"), DEFAULT("default");

    private static final Logger LOGGER = Logger.getLogger(Command.class);

    private static final Map<String, Command> commandNames = getCommandNames();

    private String customName;

    Command(String customName) {
        this.customName = customName;
    }

    private String getCustomName() {
        return customName;
    }

    /**
     * Creates a map from the names of the commands and its command object.
     */
    private static Map<String, Command> getCommandNames() {
        Map<String, Command> names = new TreeMap<>();
        for (Command command : Command.values()) {
            names.put(command.getCustomName(), command);
        }
        return names;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static Command fromString(String command) {
        Command recognized = (command == null || !commandNames.containsKey(command) ? DEFAULT
                : commandNames.get(command));
        if (recognized == DEFAULT) {
            LOGGER.warn(CustomStringJoiner.join("", "Command (", command, ") is not recognized."));
        }
        return recognized;
    }

}
