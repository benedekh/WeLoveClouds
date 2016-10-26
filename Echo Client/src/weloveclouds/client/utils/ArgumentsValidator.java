package weloveclouds.client.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import weloveclouds.client.models.commands.LogLevel;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Validates the arguments of the different commands.
 * 
 * @author Benoit, Benedek
 */
public abstract class ArgumentsValidator {
    private static final String EMPTY_MESSAGE_ERROR_MESSAGE = "Message cannot be empty (null).";
    private static final int SEND_MESSAGE_SIZE_LIMIT_IN_BYTES = 128;
    private static final int CONNECT_NUMBER_OF_ARGUMENTS = 2;
    private static final int LOG_LEVEL_NUMBER_OF_ARGUMENTS = 1;
    private static final int LEVEL_INDEX = 0;
    private static final int NETWORK_PORT_LOWER_LIMIT = 0;
    private static final int NETWORK_PORT_UPPER_LIMIT = 65536;
    private static List<String> logLevels =
            Arrays.asList("ALL", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");

    private static final Logger LOGGER = Logger.getLogger(ArgumentsValidator.class);

    /**
     * A connect command is valid, if:<br>
     * (1) the 0. parameter of arguments is a valid IP address, and <br>
     * (2) the 1st parameter of arguments i a valid port, and <br>
     * (3) these are the only arguments.<br>
     * The remoteServer already contains the IP address and the port.
     * 
     * @throws IllegalArgumentException if there is a validation error
     */
    public static void validateConnectArguments(String[] arguments,
            ServerConnectionInfo remoteServer) throws IllegalArgumentException {
        if (isNullOrEmpty(arguments) || arguments.length != CONNECT_NUMBER_OF_ARGUMENTS) {
            LOGGER.warn("Connect command is invalid.");
            throw new IllegalArgumentException(
                    "Command need arguments (<IP address> and <port>) only.");
        } else if (remoteServer.getPort() < NETWORK_PORT_LOWER_LIMIT
                || remoteServer.getPort() > NETWORK_PORT_UPPER_LIMIT) {
            String message = StringJoiner.join(",",
                    "Port should be in the range [" + String.valueOf(NETWORK_PORT_LOWER_LIMIT),
                    String.valueOf(NETWORK_PORT_UPPER_LIMIT) + "].");
            LOGGER.warn("Connect command is invalid.");
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * A send command is valid, if:<br>
     * (1) the message (arguments) is not null, and <br>
     * (2) its length is at most 128 kB.
     * 
     * @throws IllegalArgumentException if there is a validation error
     */
    public static void validateSendArguments(String[] arguments) throws IllegalArgumentException {
        if (arguments != null) {
            LOGGER.warn("Send command is invalid.");
            throw new IllegalArgumentException(EMPTY_MESSAGE_ERROR_MESSAGE);
        } else {
            byte[] messageBytes =
                    StringJoiner.join(" ", arguments).getBytes(StandardCharsets.US_ASCII);
            if (messageBytes.length > SEND_MESSAGE_SIZE_LIMIT_IN_BYTES) {
                LOGGER.warn("Send command is invalid.");
                throw new IllegalArgumentException(
                        StringJoiner.join(" ", "Message size can be at most",
                                String.valueOf(SEND_MESSAGE_SIZE_LIMIT_IN_BYTES), "kB"));
            }
        }
    }

    /**
     * A logLevel command is valid, if:<br>
     * (1) the 0. element of the arguments array is a log level, and<br>
     * (2) the log level is one of those in {@link LogLevel}.
     * 
     * @param arguments 0. element of the array contains the log level
     * @throws IllegalArgumentException if
     */
    public static void validateLogLevelArguments(String[] arguments)
            throws IllegalArgumentException {
        String message = StringJoiner.join(" ",
                "Log level is not recognized. It should be capitalized and should be one of the followings:",
                convertLevelsToString());

        if (isNullOrEmpty(arguments)) {
            LOGGER.warn("logLevel command is invalid.");
            throw new IllegalArgumentException(message);
        } else if (arguments.length > LOG_LEVEL_NUMBER_OF_ARGUMENTS) {
            LOGGER.warn("logLevel command is invalid.");
            throw new IllegalArgumentException("logLevel only accepts one parameter.");
        } else if (!logLevels.contains(arguments[LEVEL_INDEX])) {
            LOGGER.warn("logLevel command is invalid.");
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * A disconnect command is valid, if it does not contain any argument.
     * 
     * @param arguments shall be empty
     * @throws IllegalArgumentException if there is a validation error
     */
    public static void validateDisconnectArguments(String[] arguments)
            throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            LOGGER.warn("Disconnect command is invalid.");
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    /**
     * A help command is valid, if it does not contain any argument.
     * 
     * @param arguments shall be empty
     * @throws IllegalArgumentException if there is a validation error
     */
    public static void validateHelpArguments(String[] arguments) {
        if (!isNullOrEmpty(arguments)) {
            LOGGER.warn("Help command is invalid.");
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    /**
     * A disconnect quit is valid, if it does not contain any argument.
     * 
     * @param arguments shall be empty
     * @throws IllegalArgumentException if there is a validation error
     */
    public static void validateQuitArguments(String[] arguments) {
        if (!isNullOrEmpty(arguments)) {
            LOGGER.warn("Quit command is invalid.");
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    /**
     * True if the arguments parameter is either null or empty.
     */
    private static boolean isNullOrEmpty(String[] arguments) {
        return arguments == null || arguments.length == 0;
    }

    /**
     * Joins the {@link #logLevels} by a comma and converts their names to a joined string.
     */
    private static String convertLevelsToString() {
        StringBuffer buffer = new StringBuffer();
        for (String level : logLevels) {
            buffer.append(level);
            buffer.append(",");
        }
        buffer.setLength(buffer.length() - ",".length());
        return buffer.toString();
    }

}
