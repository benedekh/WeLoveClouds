package weloveclouds.server.models.commands;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Different commands which are handled by
 * {@link weloveclouds.server.models.commands.ServerCommandFactory}.
 *
 * @author Benedek
 */
public enum ServerCommand {
    CACHESIZE("cacheSize"), HELP("help"), LOGLEVEL("logLevel"), PORT("port"), START(
            "start"), STORAGEPATH(
                    "storagePath"), STRATEGY("strategy"), QUIT("quit"), DEFAULT("default");

    private static final Logger LOGGER = Logger.getLogger(ServerCommand.class);

    private String name;

    ServerCommand(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

    /**
     * Creates a map from the names of the commands and its command object.
     */
    private static Map<String, ServerCommand> getCommandNames() {
        Map<String, ServerCommand> names = new TreeMap<>();
        for (ServerCommand command : values()) {
            names.put(command.getName(), command);
        }
        return names;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static ServerCommand createCommandFromString(String commandAsString) {
        Map<String, ServerCommand> commandNames = getCommandNames();
        ServerCommand recognizedCommand =
                (commandAsString == null || !commandNames.containsKey(commandAsString) ? DEFAULT
                        : commandNames.get(commandAsString));

        if (recognizedCommand == DEFAULT) {
            LOGGER.warn(CustomStringJoiner.join("", "Command (", commandAsString,
                    ") is not recognized."));
        }

        return recognizedCommand;
    }

}

