package weloveclouds.client.utils;

import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weloveclouds.client.models.UserInput;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * @author Benoit
 */
public class UserInputParser {
    private static final int IP_ADDRESS_INDEX = 0;
    private static final int PORT_INDEX = 1;
    private static final String ARGUMENTS_SEPARATOR = " ";

    private static final Pattern INPUT_REGEX =
            Pattern.compile("(?<command>\\w+) ?" + "(?<arguments>.+)?");

    public static UserInput parse(String userInput) {
        String command = "";
        String[] arguments = {};
        Matcher matcher = INPUT_REGEX.matcher(userInput);
        if (matcher.find()) {
            if (matcher.group("command") != null) {
                command = matcher.group("command");
            }
            if (matcher.group("arguments") != null) {
                arguments = matcher.group("arguments").split(ARGUMENTS_SEPARATOR);
            }
        }
        return new UserInput.UserInputFactory().command(command).arguments(arguments).build();
    }

    public static ServerConnectionInfo extractConnectionInfoFrom(String[] arguments) throws UnknownHostException {
        String ipAddress;
        int port;
        try {
            ipAddress = arguments[IP_ADDRESS_INDEX];
            port = Integer.parseInt(arguments[PORT_INDEX]);
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Unable to extract server infos from command " +
                    "arguments");
        }

        return new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress(ipAddress).port(port)
                .build();
    }
}
