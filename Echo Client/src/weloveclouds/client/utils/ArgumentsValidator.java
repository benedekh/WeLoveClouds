package weloveclouds.client.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * @author Benedek
 */
public class ArgumentsValidator {
    private static final String EMPTY_MESSAGE_ERROR_MESSAGE = "Message cannot be empty (null).";
    private static final int CONNECT_NUMBER_OF_ARGUMENTS = 2;
    private static final int LEVEL_INDEX = 0;
    private static List<String> logLevels =
            Arrays.asList("ALL", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");

    public static void validateConnectArguments(String[] arguments, ServerConnectionInfo
            remoteServer) {
        if (ValidatorUtils.isNullOrEmpty(arguments) || arguments.length !=
                CONNECT_NUMBER_OF_ARGUMENTS) {
            throw new InvalidParameterException("Command need arguments (<IP address> and <port>)" +
                    " only");
        } else if (remoteServer.getPort() < 0 || remoteServer.getPort() > 65536) {
            throw new IllegalArgumentException("Port should be in the range [0,65536].");
        }
    }

    public static void validateSendArguments(String[] arguments) throws IllegalArgumentException {
        if (ValidatorUtils.isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException(EMPTY_MESSAGE_ERROR_MESSAGE);
        }
    }

    public static void validateLogLevelArguments(String[] arguments) throws
            IllegalArgumentException {
        if (!logLevels.contains(arguments[LEVEL_INDEX])) {
            throw new IllegalArgumentException(
                    "Log level is not recognized. It should be capitalized and should be one of the followings: All, DEBUG, INFO, WARN, ERROR, FATAL, OFF");
        }
    }

    public static void validateDisconnectArguments(String[] arguments) throws
            IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    public static void validateHelpArguments(String[] arguments) {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    public static void validateQuitArguments(String[] arguments) {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    public static boolean isNullOrEmpty(String[] arguments) {
        return arguments == null || arguments.length == 0;
    }
}
