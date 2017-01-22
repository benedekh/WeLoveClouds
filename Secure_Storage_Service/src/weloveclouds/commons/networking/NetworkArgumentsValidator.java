package weloveclouds.commons.networking;

/**
 * Created by Benoit on 2017-01-18.
 */
public class NetworkArgumentsValidator {
    private static final int NETWORK_PORT_LOWER_BOUND = 0;
    private static final int NETWORK_PORT_HIGHER_BOUND = 65536;

    public static int validateNetworkPort(String argument) throws IllegalArgumentException {
        int port;
        try {
            port = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return validateNetworkPortRange(port);
    }

    public static int validateNetworkPort(int port) throws IllegalArgumentException {
        return validateNetworkPortRange(port);
    }

    private static int validateNetworkPortRange(int port) {
        if (port < NETWORK_PORT_LOWER_BOUND || port > NETWORK_PORT_HIGHER_BOUND) {
            throw new IllegalArgumentException("Invalid network port: " + Integer.toString(port) +
                    " a network port has to be in range <0-65536>");
        }
        return port;
    }
}
