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
            Pattern.compile("(?<command>\\w+) " + "(?<arguments>\\.+)");

    public static UserInput parse(String userInput) {
        Matcher matcher = INPUT_REGEX.matcher(userInput);
        matcher.find();
        return new UserInput.UserInputFactory().command(matcher.group("command"))
                .arguments(matcher.group("arguments").split(ARGUMENTS_SEPARATOR)).build();
    }

    public static ServerConnectionInfo extractConnectionInfoFrom(String[] arguments) throws UnknownHostException{
        String ipAddress = arguments[IP_ADDRESS_INDEX];
        int port = Integer.parseInt(arguments[PORT_INDEX]);

        return new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress(ipAddress).port(port)
                .build();
    }
}
