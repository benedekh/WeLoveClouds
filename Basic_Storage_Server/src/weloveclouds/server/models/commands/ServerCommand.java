package weloveclouds.server.models.commands;

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
public enum ServerCommand {
    CACHESIZE("cacheSize"), HELP("help"), LOGLEVEL("logLevel"), PORT("port"), START(
            "start"), STORAGEPATH(
                    "storagePath"), STRATEGY("strategy"), QUIT("quit"), DEFAULT("default");

    private static final Logger LOGGER = Logger.getLogger(ServerCommand.class);

    private static final Map<String, ServerCommand> commandNames = getCommandNames();

    private String customName;

    ServerCommand(String customName) {
        this.customName = customName;
    }

    private String getCustomName() {
        return customName;
    }

    /**
     * Creates a map from the names of the commands and its command object.
     */
    private static Map<String, ServerCommand> getCommandNames() {
        Map<String, ServerCommand> names = new TreeMap<>();
        for (ServerCommand command : values()) {
            names.put(command.getCustomName(), command);
        }
        return names;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static ServerCommand fromString(String command) {
        ServerCommand recognized = (command == null || !commandNames.containsKey(command) ? DEFAULT
                : commandNames.get(command));
        if (recognized == DEFAULT) {
            LOGGER.warn(CustomStringJoiner.join("", "Command (", command, ") is not recognized."));
        }
        return recognized;
    }

}

