package weloveclouds.client.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Different commands which are handled by
 * {@link weloveclouds.client.models.commands.CommandFactory}.
 *
 * @author Benedek
 */
public enum ClientCommand {
    CONNECT("connect"), DISCONNECT("disconnect"), PUT("put"), GET("get"), LOGLEVEL(
            "logLevel"), HELP("help"), QUIT("quit"), DEFAULT("default");

    private static final Logger LOGGER = Logger.getLogger(ClientCommand.class);

    private String description;

    ClientCommand(String description) {
        this.description = description;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static ClientCommand createCommandFromString(String description) {
        for (ClientCommand command : ClientCommand.values()) {
            if (command.description.equals(description)) {
                return command;
            }
        }

        LOGGER.warn(CustomStringJoiner.join("", "Command (", description, ") is not recognized."));
        return DEFAULT;
    }

}
