package weloveclouds.client.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * @author Benedek
 */
public abstract class ArgumentsValidator {
    private static final String EMPTY_MESSAGE_ERROR_MESSAGE = "Message cannot be empty (null).";
    private static final int SEND_MESSAGE_SIZE_LIMIT_IN_BYTES = 128;
    private static final int CONNECT_NUMBER_OF_ARGUMENTS = 2;
    private static final int LEVEL_INDEX = 0;
    private static final int NETWORK_PORT_LOWER_LIMIT = 0;
    private static final int NETWORK_PORT_UPPER_LIMIT = 65536;
    private static List<String> logLevels =
            Arrays.asList("ALL", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");

    private static final Logger LOGGER =
            Logger.getLogger(ArgumentsValidator.class);

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

    public static void validateSendArguments(String[] arguments) throws IllegalArgumentException {
        if (isNullOrEmpty(arguments)) {
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

    public static void validateLogLevelArguments(String[] arguments)
            throws IllegalArgumentException {
        if (!logLevels.contains(arguments[LEVEL_INDEX])) {
            LOGGER.warn("setLevel command is invalid.");
            String message = StringJoiner.join(" ",
                    "Log level is not recognized. It should be capitalized and should be one of the followings:",
                    convertLevelsToString());
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateDisconnectArguments(String[] arguments)
            throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            LOGGER.warn("Disconnect command is invalid.");
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    public static void validateHelpArguments(String[] arguments) {
        if (!isNullOrEmpty(arguments)) {
            LOGGER.warn("Help command is invalid.");
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    public static void validateQuitArguments(String[] arguments) {
        if (!isNullOrEmpty(arguments)) {
            LOGGER.warn("Quit command is invalid.");
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    public static boolean isNullOrEmpty(String[] arguments) {
        return arguments == null || arguments.length == 0;
    }

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
