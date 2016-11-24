package weloveclouds.client.models.commands;

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
    CONNECT("connect"), DISCONNECT("disconnect"), PUT("put"), GET("get"), LOGLEVEL(
            "logLevel"), HELP("help"), QUIT("quit"), DEFAULT("default");

    private static final Logger LOGGER = Logger.getLogger(Command.class);

    private String name;

    Command(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

    /**
     * Creates a map from the names of the commands and its command object.
     */
    private static Map<String, Command> getCommandNames() {
        Map<String, Command> names = new TreeMap<>();
        for (Command command : Command.values()) {
            names.put(command.getName(), command);
        }
        return names;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static Command createCommandFromString(String commandAsString) {
        Map<String, Command> commandNames = getCommandNames();
        Command recognizedCommand =
                (commandAsString == null || !commandNames.containsKey(commandAsString) ? DEFAULT
                        : commandNames.get(commandAsString));

        if (recognizedCommand == DEFAULT) {
            LOGGER.warn(CustomStringJoiner.join("", "Command (", commandAsString,
                    ") is not recognized."));
        }

        return recognizedCommand;
    }

}
