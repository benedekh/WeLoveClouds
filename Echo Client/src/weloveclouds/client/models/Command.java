package weloveclouds.client.models;

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

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static Command fromString(String command) {
        Command enumValue;
        try {
            // TODO refactor it for exact match not toUppercase!!!!
            enumValue = Command.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException ex) {
            enumValue = DEFAULT;
            LOGGER.warn(StringJoiner.join("", "Command (", command, ") is not recognized."));
        }
        return enumValue;
    }
}
