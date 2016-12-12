package weloveclouds.ecs.models.commands.client;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Created by Benoit on 2016-11-20.
 */
public enum EcsCommand {
    START("start"), STOP("stop"), INIT_SERVICE("initService"), SHUTDOWN("shutDown"),
    ADD_NODE("addNode"), REMOVE_NODE("removeNode"), DEFAULT("default"), QUIT("quit"),
    LOGLEVEL("logLevel"), HELP("help");

    private static final Logger LOGGER = Logger.getLogger(EcsCommand.class);

    private String name;

    EcsCommand(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

    /**
     * Creates a map from the names of the commands and its command object.
     */
    private static Map<String, EcsCommand> getCommandNames() {
        Map<String, EcsCommand> names = new TreeMap<>();
        for (EcsCommand command : EcsCommand.values()) {
            names.put(command.getName(), command);
        }
        return names;
    }

    public static EcsCommand createCommandFromString(String commandAsString) {
        Map<String, EcsCommand> commandNames = getCommandNames();
        EcsCommand recognizedCommand =
                (commandAsString == null || !commandNames.containsKey(commandAsString) ? DEFAULT
                        : commandNames.get(commandAsString));

        if (recognizedCommand == DEFAULT) {
            LOGGER.warn(CustomStringJoiner.join("", "Command (", commandAsString,
                    ") is not recognized."));
        }

        return recognizedCommand;
    }
}
