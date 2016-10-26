package weloveclouds.client.utils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import weloveclouds.client.models.ParsedUserInput;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Parses the raw user input into a processed user input so it can be handled by the application
 * more efficiently.
 * 
 * @author Benoit
 */
public class UserInputParser {
    private static final int IP_ADDRESS_INDEX = 0;
    private static final int PORT_INDEX = 1;
    private static final String ARGUMENTS_SEPARATOR = " ";

    private static final Pattern INPUT_REGEX =
            Pattern.compile("(?<command>\\w+) ?" + "(?<arguments>.+)?");

    private static final Logger LOGGER = Logger.getLogger(UserInputParser.class);

    /**
     * Extracts the <command> and its <arguments> from the user input.
     * 
     * @param userInput the raw content of the user input (raw line from the user input stream)
     * @return the extracted command and its arguments stored in one object
     */
    public static ParsedUserInput parse(String userInput) {
        String command = "";
        String[] arguments = {};
        Matcher matcher = INPUT_REGEX.matcher(userInput);
        if (matcher.find()) {
            if (matcher.group("command") != null) {
                command = matcher.group("command");
                LOGGER.debug(CustomStringJoiner.join(" ", command, "is parsed."));
            }
            if (matcher.group("arguments") != null) {
                arguments = matcher.group("arguments").split(ARGUMENTS_SEPARATOR);

                List<String> debugMessages = new ArrayList<>();
                debugMessages.add("Arguments are recognized for the command:");
                debugMessages.addAll(Arrays.asList(arguments));
                LOGGER.debug(CustomStringJoiner.join(" ", debugMessages));
            }
        }
        return new ParsedUserInput.ParsedUserInputBuilder().command(command).arguments(arguments)
                .build();
    }

    /**
     * Extracts the IP address and port from the arguments array. <br>
     * The IP address shall be the 0. element of the array.<br>
     * The port shall be the 1. element of the array.
     * 
     * @return the IP address and port stored in one server conenction information object
     * @throws UnknownHostException see
     *         {@link ServerConnectionInfo.ServerConnectionInfoBuilder#ipAddress(java.net.InetAddress)}
     */
    public static ServerConnectionInfo extractConnectionInfoFrom(String[] arguments)
            throws UnknownHostException {
        try {
            LOGGER.debug("Extracting IP address and port from arguments.");
            String ipAddress = arguments[IP_ADDRESS_INDEX];
            int port = Integer.parseInt(arguments[PORT_INDEX]);

            ServerConnectionInfo connectionInfo =
                    new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress(ipAddress)
                            .port(port).build();
            LOGGER.debug(CustomStringJoiner.join(" ", "Connection parameters are extracted",
                    connectionInfo.toString()));
            return connectionInfo;
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalArgumentException(
                    "Unable to extract server connection parameters from command arguments.");
        }
    }
}
