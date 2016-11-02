package weloveclouds.client.utils;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import weloveclouds.client.models.commands.LogLevel;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.serialization.models.SerializedKVMessage;

/**
 * Validates the arguments of the different commands.
 *
 * @author Benoit, Benedek
 */
public class ArgumentsValidator {
    private static final int KEY_SIZE_LIMIT_IN_BYTES = 20;
    private static final int VALUE_SIZE_LIMIT_IN_BYTES = 120 * 1000;

    private static final int CONNECT_NUMBER_OF_ARGUMENTS = 2;
    private static final int LOG_LEVEL_NUMBER_OF_ARGUMENTS = 1;
    private static final int GET_NUMBER_OF_ARGUMENTS = 1;
    private static final int PUT_MIN_NUMBER_OF_ARGUMENTS = 1;

    private static final int LEVEL_INDEX = 0;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int NETWORK_PORT_LOWER_LIMIT = 0;
    private static final int NETWORK_PORT_UPPER_LIMIT = 65535;

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
        String command = "connect";

        if (isNullOrEmpty(arguments) || arguments.length != CONNECT_NUMBER_OF_ARGUMENTS) {
            logWarning(command);
            throw new IllegalArgumentException(
                    "Command need arguments (<IP address> and <port>) only.");
        } else if (remoteServer.getPort() < NETWORK_PORT_LOWER_LIMIT
                || remoteServer.getPort() > NETWORK_PORT_UPPER_LIMIT) {
            String message = join("", "Port should be in the range [",
                    String.valueOf(NETWORK_PORT_LOWER_LIMIT), ",",
                    String.valueOf(NETWORK_PORT_UPPER_LIMIT) + "].");
            logWarning(command);
            throw new IllegalArgumentException(message);
        }
    }

    public static void validatePutArguments(String[] arguments) throws IllegalArgumentException {
        String command = "put";

        if (isNullOrEmpty(arguments) || arguments.length < PUT_MIN_NUMBER_OF_ARGUMENTS) {
            logWarning(command);
            throw new IllegalArgumentException("Put command should have at least two arguments.");
        } else {
            validateSize(arguments[KEY_INDEX], KEY_SIZE_LIMIT_IN_BYTES, command, "key");

            // merge values to one string
            List<String> argList = Arrays.asList(arguments);
            List<String> valueElements = argList.subList(VALUE_INDEX, argList.size());
            String value = CustomStringJoiner.join(" ", valueElements);

            validateSize(value, VALUE_SIZE_LIMIT_IN_BYTES, command, "value");
        }
    }

    public static void validateGetArguments(String[] arguments) throws IllegalArgumentException {
        String command = "get";
        if (isNullOrEmpty(arguments) || arguments.length != GET_NUMBER_OF_ARGUMENTS) {
            logWarning(command);
            throw new IllegalArgumentException("Get command should have one argument.");
        } else {
            validateSize(arguments[KEY_INDEX], KEY_SIZE_LIMIT_IN_BYTES, command, "key");
        }
    }

    private static void validateSize(String field, int limit, String commandName, String fieldName)
            throws IllegalArgumentException {
        byte[] key = field.getBytes(SerializedKVMessage.MESSAGE_ENCODING);
        if (key.length > limit) {
            logWarning(commandName);
            throw new IllegalArgumentException(
                    join(" ", "Max", fieldName, "size is", String.valueOf(limit), "bytes."));
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
        String command = "logLevel";
        String message = join(" ",
                "Log level is not recognized. It should be capitalized and should be one of the followings:",
                join(",", logLevels));

        if (isNullOrEmpty(arguments) || arguments.length != LOG_LEVEL_NUMBER_OF_ARGUMENTS) {
            logWarning(command);
            throw new IllegalArgumentException(message);
        } else if (!logLevels.contains(arguments[LEVEL_INDEX])) {
            logWarning(command);
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
            logWarning("disconnect");
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    /**
     * A help command is valid, if it does not contain any argument.
     *
     * @param arguments shall be empty
     * @throws IllegalArgumentException if there is a validation error
     */
    public static void validateHelpArguments(String[] arguments) throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            logWarning("help");
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    /**
     * A quit command is valid, if it does not contain any argument.
     *
     * @param arguments shall be empty
     * @throws IllegalArgumentException if there is a validation error
     */
    public static void validateQuitArguments(String[] arguments) throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            logWarning("quit");
            throw new IllegalArgumentException("Command does not accept any argument.");
        }
    }

    /**
     * True if the arguments parameter is either null or empty.
     */
    private static boolean isNullOrEmpty(String[] arguments) {
        return arguments == null || arguments.length == 0;
    }

    private static void logWarning(String command) {
        String warning = join(" ", command, "command is invalid.");
        LOGGER.warn(warning);
    }
}
