package weloveclouds.server.utils;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import weloveclouds.client.models.commands.LogLevel;
import weloveclouds.server.models.ServerConfigurationContext;
import weloveclouds.server.models.commands.ServerCommand;

/**
 * Validates the arguments of the different commands ({@link ServerCommand}).
 *
 * @author Benedek
 */
public class ArgumentsValidator {

    private static final Logger LOGGER = Logger.getLogger(ArgumentsValidator.class);

    private static final int LOG_LEVEL_NUMBER_OF_ARGUMENTS = 1;
    private static final int LOG_LEVEL_INDEX = 0;
    private static List<String> validLogLevels =
            Arrays.asList("ALL", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");

    private static final int PORT_NUMBER_OF_ARGUMENTS = 1;
    private static final int PORT_INDEX = 0;
    private static final int PORT_LOWER_LIMIT = 0;
    private static final int PORT_UPPER_LIMIT = 65535;

    private static final int STRATEGY_NUMBER_OF_ARGUMENTS = 1;
    private static final int STRATEGY_INDEX = 0;
    private static List<String> validStrategyNames = Arrays.asList("FIFO", "LFU", "LRU");

    private static final int CACHE_SIZE_NUMBER_OF_ARGUMENTS = 1;
    private static final int CACHE_SIZE_INDEX = 0;

    private static final int STORAGE_PATH_NUMBER_OF_ARGUMENTS = 1;
    private static final int STORAGE_PATH_INDEX = 0;


    /**
     * A start command is valid, if:<br>
     * (1) it does not have any arguments, and <br>
     * (2) every field of the context object was initialized.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateStartArguments(String[] arguments,
            ServerConfigurationContext context) throws IllegalArgumentException {
        String command = "start";
        if (!isNullOrEmpty(arguments)) {
            logWarning(command);
            throw new IllegalArgumentException("Command does not accept any argument.");
        } else {
            if (context.isStarted()) {
                logWarning(command);
                throw new IllegalArgumentException("Server is already running.");
            } else if (context.getCacheSize() == -1) {
                logWarning(command);
                throw new IllegalArgumentException("Cache size should be initialized first.");
            } else if (context.getPort() == -1) {
                logWarning(command);
                throw new IllegalArgumentException("Port should be initialized first.");
            } else if (context.getDisplacementStrategy() == null) {
                logWarning(command);
                throw new IllegalArgumentException(
                        "Cache displacement strategy should be initialized first.");
            } else if (context.getStoragePath() == null) {
                logWarning(command);
                throw new IllegalArgumentException(
                        "Persistent storage path should be initialized first.");
            }
        }
    }

    /**
     * A storage path command is valid, if:<br>
     * (1) the {@link #STORAGE_PATH_INDEX} location parameter of arguments is a valid path, and <br>
     * (2) this is the only argument of the command
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateStoragePathArguments(String[] arguments)
            throws IllegalArgumentException {
        String command = "storagePath";
        if (isNullOrEmpty(arguments) || arguments.length != STORAGE_PATH_NUMBER_OF_ARGUMENTS) {
            logWarning(command);
            throw new IllegalArgumentException("storagePath need one argument (<path>) only.");
        } else {
            try {
                Path path = Paths.get(arguments[STORAGE_PATH_INDEX]);
                if (!path.toAbsolutePath().toFile().exists()) {
                    logWarning(command);
                    throw new IllegalArgumentException("Path does not exist.");
                }
            } catch (InvalidPathException ex) {
                logWarning(command);
                throw new IllegalArgumentException(ex);
            }
        }
    }

    /**
     * A cache size command is valid, if:<br>
     * (1) the {@link #CACHE_SIZE_INDEX} location parameter of arguments is a valid number, and <br>
     * (2) this is the only argument of the command
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateCacheSizeArguments(String[] arguments)
            throws IllegalArgumentException {
        String command = "cacheSize";

        if (isNullOrEmpty(arguments) || arguments.length != CACHE_SIZE_NUMBER_OF_ARGUMENTS) {
            logWarning(command);
            throw new IllegalArgumentException("cacheSize need one argument (<size>) only.");
        } else {
            try {
                Integer.parseInt(arguments[CACHE_SIZE_INDEX]);
            } catch (NumberFormatException ex) {
                logWarning(command);
                throw new IllegalArgumentException("Cache size has to be a number.");
            }
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
     * A port command is valid, if:<br>
     * (1) the {@link #PORT_SIZE_INDEX} location parameter of arguments is a valid port number, and
     * <br>
     * (2) this is the only argument of the command
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validatePortArguments(String[] arguments) throws IllegalArgumentException {
        String command = "port";

        if (isNullOrEmpty(arguments) || arguments.length != PORT_NUMBER_OF_ARGUMENTS) {
            logWarning(command);
            throw new IllegalArgumentException("Port need one argument (<port>) only.");
        } else {
            try {
                int port = Integer.parseInt(arguments[PORT_INDEX]);

                if (port < PORT_LOWER_LIMIT || port > PORT_UPPER_LIMIT) {
                    String message = join("", "Port should be in the range [",
                            String.valueOf(PORT_LOWER_LIMIT), ",",
                            String.valueOf(PORT_UPPER_LIMIT) + "].");
                    logWarning(command);
                    throw new IllegalArgumentException(message);
                }
            } catch (NumberFormatException ex) {
                logWarning(command);
                throw new IllegalArgumentException("Port has to be a number.");
            }
        }
    }

    /**
     * A logLevel command is valid, if:<br>
     * (1) the {@link #LOG_LEVEL_INDEX} element of the arguments array is a log level, and<br>
     * (2) the log level is one of those in {@link LogLevel}.
     *
     * @param arguments {@link #LOG_LEVEL_INDEX} element of the array contains the log level
     * @throws IllegalArgumentException if
     */
    public static void validateLogLevelArguments(String[] arguments)
            throws IllegalArgumentException {
        String command = "logLevel";
        String message = join(" ",
                "Log level is not recognized. It should be capitalized and should be one of the followings:",
                join(",", validLogLevels));

        if (isNullOrEmpty(arguments) || arguments.length != LOG_LEVEL_NUMBER_OF_ARGUMENTS) {
            logWarning(command);
            throw new IllegalArgumentException(message);
        } else if (!validLogLevels.contains(arguments[LOG_LEVEL_INDEX])) {
            logWarning(command);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * A strategy command is valid, if:<br>
     * (1) the {@link #STRATEGY_INDEX} location parameter of arguments is a name for a displacement
     * startegy, and <br>
     * (2) this is the only argument of the command
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateStrategyArguments(String[] arguments)
            throws IllegalArgumentException {
        String command = "strategy";
        String message = join(" ",
                "Strategy is not recognized. It should be capitalized and should be one of the followings:",
                join(",", validStrategyNames));

        if (isNullOrEmpty(arguments) || arguments.length != STRATEGY_NUMBER_OF_ARGUMENTS) {
            logWarning(command);
            throw new IllegalArgumentException(message);
        } else if (!validStrategyNames.contains(arguments[STRATEGY_INDEX])) {
            logWarning(command);
            throw new IllegalArgumentException(message);
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
     * True if the arguments parameter is either null or empty.
     */
    private static boolean isNullOrEmpty(String[] arguments) {
        return arguments == null || arguments.length == 0;
    }

    /**
     * Logs a warning message by the logger.
     * 
     * @param command which command sent the warning
     */
    private static void logWarning(String command) {
        String warning = join(" ", command, "command is invalid.");
        synchronized (LOGGER) {
            LOGGER.warn(warning);
        }
    }

}
