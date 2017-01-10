package weloveclouds.ecs.utils;

public class HelpMessageGenerator {

    /**
     * @return A help message for the ecs client, as a string
     * 
     */
    public static String generateHelpMessage() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ECS Client\n");
        buffer.append("\n");
        buffer.append("A tool for managing a collection of Key Value servers\n");
        buffer.append("\n");
        buffer.append("First, the user shall initialize the storage service\n");
        buffer.append("Second, the user shall start the service, making it ready to handle connections from clients\n");
        buffer.append("From there, the user may add or remove nodes from the storage service as they please\n");
        buffer.append("Finally, the user can stop the storage service, shut it down and exit the ECS program\n");
        buffer.append("\n");
        buffer.append("|Commmand | Parameters | Description | Expected shell output |\n");
        String delimeterLine = "|--------|------------|-------------|----------------------|\n";
        buffer.append(delimeterLine);
        buffer.append("|initservice <Number of Nodes> <Cache Size> <Displacement Strategy>| <Number of Nodes>: The quantity of randomly selected servers to start KVServer on. | <Cache Size>: Size of the cache on each server. | <Displacement Strategy>: The displacemeny strategy that each server will use, can be FIFO, LFU, or LRU. |\n");
        buffer.append("|start | (none) | Causes the storage service to begin accepting client and ECS requests. |\n");
        buffer.append("|stop | (none) |  Causes the storage service to stop accepting client and ECS requests. |\n");
        buffer.append("|help | (none) | Shows the intended usage of the ECS client application and describes its set of commands. |\n");
        buffer.append("|logLevel <level> | <level>: One of the following log4j log levels: ALL, DEBUG, INFO, WARN, ERROR, FATAL, OFF | Sets the logger to the specified log level. | Shows the most recent log status. |\n");
        buffer.append("|shutdown | (none) | Stops the storage service and exits all remote processes. |\n");
        buffer.append("|quit | (none) | Tears down the storage service, exits remote processes, and exits the program execution. | Shows a status message about the imminent program shutdown. |\n");
        buffer.append("|removenode | (none) | removes an arbitrary node from the storage service. |\n");
        buffer.append("|addnode | <Cache Size> <Displacement Strategy>| <Cache Size>: The cache size for a new node in the storage service. | <Displacement Strategy>: The displacement strategy for a new node in the storage service, the strategy can be one of; FIFO, LRU, or LFU. |\n");
        buffer.append("|<anything else> | <any> | Any unrecognized input in the context of this application. | Shows an error message and prints the same help text as for the help command. |\n");
        buffer.append(delimeterLine);
            return buffer.toString();
    }

}
