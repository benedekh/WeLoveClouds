package weloveclouds.server.client.commands.utils;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.configuration.models.KVServerPortConstants;

/**
 * Generates the help message for the application.
 *
 * @author Benedek, Hunton
 */
public class HelpMessageGenerator {

    /**
     * The help message for the application.
     */
    public static String generateHelpMessage() {
        // @formatter:off
        StringBuffer buffer = new StringBuffer();
        buffer.append("Server\n");
        buffer.append("\n");
        buffer.append("Starts a server which will can accept Key-Value pairs and store them in both a persistent storage and a cache.\n");
        buffer.append("\n");
        buffer.append("First, the user shall set how big the cache would be.\n");
        buffer.append(StringUtils.join("","Second, the user shall set on which port should the server be available to the KVClients. Default value is: ", KVServerPortConstants.KVCLIENT_REQUESTS_PORT, "\n"));
        buffer.append(StringUtils.join("","Third, the user shall set on which port should the server be available to the KVCServers. Default value is: ", KVServerPortConstants.KVSERVER_REQUESTS_PORT, "\n"));
        buffer.append(StringUtils.join("","Fourth, the user shall set on which port should the server be available to the KVECS. Default value is: ", KVServerPortConstants.KVECS_REQUESTS_PORT, "\n"));
        buffer.append("Fifth, the user shall set the path for the persistent storage. Beware, the path has to exist in the file system.\n");
        buffer.append("Sixth, the user shall set the cache displacement startegy. Possible values: FIFO, LFU, LRU.\n");
        buffer.append("Fifth, the user shall can start the server.\n");
        buffer.append("Finally, the user can close the application using the quit command.\n");
        buffer.append("\n");
        buffer.append("|Commmand | Parameters | Description | Expected shell output |\n");
        String delimeterLine = "|--------|------------|-------------|----------------------|\n";
        buffer.append(delimeterLine);
        buffer.append("|cacheSize <size> | <size>: how many entries shall be stored in the cache | Sets the size of the cache. | Latest cache size. |\n");
        buffer.append("|help | (none) | Shows the intended usage of the application and describes its set of commands. | Shows the intended usage of the application and describes its set of commands. |\n");
        buffer.append("|logLevel <level> | <level>: One of the following log4j log levels: ALL, DEBUG, INFO, WARN, ERROR, FATAL, OFF | Sets the logger to the specified log level. | Shows the most recent log status. |\n");
        buffer.append("|clientPort <port> | <port>: on which the server will accept KVClients (valid range: [0,65535]) | Sets the port on which the server accepts KVClients. | Latest port. |\n");
        buffer.append("|serverPort <port> | <port>: on which the server will accept KVServers (valid range: [0,65535]) | Sets the port on which the server accepts KVServers. | Latest port. |\n");
        buffer.append("|ecsPort <port> | <port>: on which the server will accept KVECS (valid range: [0,65535]) | Sets the port on which the server accepts KVECS. | Latest port. |\n");
        buffer.append("|start | (none) | Starts the server with the previously applied settings. Beware to use the configuration commands (cacheSize, port, strategy, storagePath) beforehand.| Status message about the server start. |\n");
        buffer.append("|storagePath <path> | <path>: where the persistent storage will store the key-value pairs. | A valid folder path in the file system. | Latest path. |\n");
        buffer.append("|strategy <strategy> | <strategy>: the abbreviation of the strategy that shall be used for displacing entries from the cache. (valid values: FIFO, LFU, LRU) | Sets what values shall be used to displace entries from the cache. | Latest strategy. |\n");
        buffer.append("|quit | (none) | Tears down the active connections and exits the program execution. | Shows a status message about the imminent program shutdown. |\n");
        buffer.append("|<anything else> | <any> | Any unrecognized input in the context of this application. | Shows an error message and prints the same help text as for the help command. |\n");
        buffer.append(delimeterLine);
        // @formatter:on
        return buffer.toString();
    }

}
