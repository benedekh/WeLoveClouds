package weloveclouds.ecs.utils;

import java.util.Arrays;
import java.util.List;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.ecs.models.commands.client.AddNode;
import weloveclouds.ecs.models.commands.client.InitService;

/**
 * Created by Benoit on 2016-11-21. Added to by hb
 */
public class ArgumentsValidator {
    private static final int INIT_SERVICE_NUMBER_OF_ARGUMENTS = 3;
    private static final int ADD_NODE_NUMBER_OF_ARGUMENTS = 2;
    private static final int LOGLEVEL_NUMBER_OF_ARGUMENTS = 1;
    private static List<String> validStrategyNames = Arrays.asList("FIFO", "LFU", "LRU");
    private static List<String> validLogLevels =
            Arrays.asList("ALL", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");

    public static void validateStartArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Start command doesn't accept any arguments.");
        }
    }

    public static void validateStatsArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Stats command doesn't accept any arguments.");
        }
    }

    public static void validateStartLoadBalancerArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Start Load Balancer command doesn't accept any " +
                    "arguments.");
        }
    }

    public static void validateQuitArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Quit command doesn't accept any arguments.");
        }
    }

    public static void validateHelpArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Help command doesn't accept any arguments.");
        }
    }

    public static void validateStopArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Stop command doesn't accept any arguments.");
        }
    }

    public static void validateInitServiceArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (arguments.size() != INIT_SERVICE_NUMBER_OF_ARGUMENTS) {
            throw new IllegalArgumentException("InitService command takes "
                    + INIT_SERVICE_NUMBER_OF_ARGUMENTS + " arguments (Mumber of nodes(integer), "
                    + "cache size (integer) and displacement stragegy" + validStrategyNames + "). "
                    + arguments.size() + " arguments provided");
        }
        validateNumberOfNode(arguments.get(InitService.NUMBER_OF_NODES_ARG_INDEX));
        validateCacheSize(arguments.get(InitService.CACHE_SIZE_ARG_INDEX));
        validateDisplacementStrategy(arguments.get(InitService.DISPLACEMENT_STRATEGY_ARG_INDEX));
    }

    public static void validateRemoveNodeArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("RemoveNode command doesn't accept any arguments.");
        }
    }

    public static void validateShutdownArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (!isNullOrEmpty(arguments)) {
            throw new IllegalArgumentException("Shutdown command doesn't accept any arguments.");
        }
    }

    public static void validateAddNodeArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (arguments.size() != ADD_NODE_NUMBER_OF_ARGUMENTS) {
            throw new IllegalArgumentException(
                    "AddNode command takes " + ADD_NODE_NUMBER_OF_ARGUMENTS + "arguments. "
                            + arguments.size() + " " + "arguments provided");
        }
        validateCacheSize(arguments.get(AddNode.CACHE_SIZE_ARG_INDEX));
        validateDisplacementStrategy(arguments.get(AddNode.DISPlACEMENT_STRATEGY_ARG_INDEX));
    }

    private static void validateNumberOfNode(String argument) throws IllegalArgumentException {
        if (!isInteger(argument)) {
            throw new IllegalArgumentException("The number of node should be an integer.");
        }
    }

    private static void validateCacheSize(String argument) throws IllegalArgumentException {
        if (!isInteger(argument)) {
            throw new IllegalArgumentException("The cache size should be an integer.");
        }
    }

    private static void validateDisplacementStrategy(String argument)
            throws IllegalArgumentException {
        String message = StringUtils.join(" ",
                "Strategy is not recognized. It should be capitalized and should be one of the following:",
                StringUtils.join(",", validStrategyNames));
        if (!validStrategyNames.contains(argument)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateLogLevelArguments(List<String> arguments)
            throws IllegalArgumentException {
        if (arguments.size() != LOGLEVEL_NUMBER_OF_ARGUMENTS
                || !validLogLevels.contains(arguments.get(0))) {
            String msg = StringUtils.join("", "Log level not recognized, should be one of: ",
                    StringUtils.join(",", validLogLevels));
            throw new IllegalArgumentException(msg);
        }
    }

    private static boolean isInteger(String argument) {
        boolean isInteger;
        try {
            Integer.parseInt(argument);
            isInteger = true;
        } catch (NumberFormatException ex) {
            isInteger = false;
        }

        return isInteger;
    }

    private static boolean isNullOrEmpty(List<String> arguments) {
        return arguments == null || arguments.isEmpty();
    }
}
