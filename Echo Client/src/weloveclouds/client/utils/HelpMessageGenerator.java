package weloveclouds.client.utils;

/**
 * Generates the help message for the application.
 * 
 * @author Benedek
 */
public class HelpMessageGenerator {

    /**
     * The help message for the application.
     */
    public static String generateHelpMessage() {
        // @formatter:off
        StringBuffer buffer = new StringBuffer();
        buffer.append("Echo Client\n");
        buffer.append("\n");
        buffer.append("A simple tool to connect to an echo server and send and receiveFrom message from that, using the reliable TCP/IP protocol.\n");
        buffer.append("\n");
        buffer.append("First, the user shall connect to an echo server by providing its IP address and port.\n");
        buffer.append("Second, the user shall send several messages after each other to the server. The server's echo responses will be displayed.\n");
        buffer.append("Third, the user can disconnect from the server by the respective command.\n");
        buffer.append("Finally, the user can close the application using the quit command.\n");
        buffer.append("\n");
        buffer.append("|Commmand | Parameters | Description | Expected shell output |\n");
        String delimeterLine = "|--------|------------|-------------|----------------------|\n";
        buffer.append(delimeterLine);
        buffer.append("|connect <address> <port> | <address>: Hostname or IP address of the echo server. <port>: The port of the echo service on the respective server. | Tries to establish a TCP-connection to the echo server based on the given server address and the port number of the echo service. | Once the connection is established, the echo server will reply with a confirmation message. This message will be displayed to the user. |\n");
        buffer.append("|disconnect | (none) | Tries to disconnect from the connected server. | Once the client got disconnected from the server, it will provide a notification to the user. |\n");
        buffer.append("|send <message> | <message>: Sequence of ASCII coded characters that correspond to the application specific protocol. | Sends a text message to the echo server according to the communication protocol. | Once the echo server received the message it will send back the same message to the client. This message will be displayed in a new line.|\n");
        buffer.append("|logLevel <level> | <level>: One of the following log4j log levels: ALL, DEBUG, INFO, WARN, ERROR, FATAL, OFF | Sets the logger to the specified log level. | Shows the most recent log status. |\n");
        buffer.append("|help | (none) | Shows the intended usage of the client application and describes its set of commands. | Shows the intended usage of the client application and describes its set of commands. |\n");
        buffer.append("|quit | (none) | Tears down the active connection to the server and exits the program execution. | Shows a status message about the imminent program shutdown. |\n");
        buffer.append("|<anything else> | <any> | Any unrecognized input in the context of this application. | Shows an error message and prints the same help text as for the help command. |\n");
        buffer.append(delimeterLine);
        // @formatter:on
        return buffer.toString();
    }

}
