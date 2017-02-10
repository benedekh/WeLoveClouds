package weloveclouds.client.commands;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;

/**
 * Different commands which are handled by {@link CommandFactory}.
 *
 * @author Benedek, Hunton
 */
public enum ClientCommand {
    CONNECT("connect"), DISCONNECT("disconnect"), PUT("put"), GET("get"), LOGLEVEL(
            "logLevel"), HELP("help"), QUIT("quit"), DEFAULT("default");

    private static final Logger LOGGER = Logger.getLogger(ClientCommand.class);

    private String description;

    private ClientCommand(String description) {
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

        LOGGER.warn(StringUtils.join("", "Command (", description, ") is not recognized."));
        return DEFAULT;
    }

}
