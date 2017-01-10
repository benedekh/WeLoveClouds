package weloveclouds.client.commands;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.commons.cli.models.ParsedUserInput;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;


/**
 * CommandFactory design pattern, which gives a common handling mechanism of different commands. It
 * handles several commands (see {@link ClientCommand} for the possible commands) by dispatching the
 * command to its respective handler.
 *
 * @author Benoit
 */
public class CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(CommandFactory.class);

    private IKVCommunicationApiV2 communicationApi;
    private IDeserializer<RingMetadata, String> ringMetadataDeserializer;

    /**
     * @param communicationApi an instance for the communication module for those commands which
     *        need to communicate via the network
     * @param ringMetadataDeserializer deserializer that converts a {@link RingMetadata} object to
     *        its original representation from String
     */
    public CommandFactory(IKVCommunicationApiV2 communicationApi,
            IDeserializer<RingMetadata, String> ringMetadataDeserializer) {
        this.communicationApi = communicationApi;
        this.ringMetadataDeserializer = ringMetadataDeserializer;
    }

    /**
     * Dispatches the command that is stored in the userInput to its respective handler, which
     * processes it.
     *
     * @param userInput which contains the command and its arguments
     * @return the type of the recognized command
     * @throws UnknownHostException see {@link Connect}
     */
    public ICommand createCommandFromUserInput(ParsedUserInput<ClientCommand> userInput)
            throws UnknownHostException {
        ICommand recognizedCommand = null;
        ClientCommand userCommand = userInput.getCommand();

        switch (userCommand) {
            case CONNECT:
                recognizedCommand = new Connect(userInput.getArguments(), communicationApi);
                break;
            case DISCONNECT:
                recognizedCommand = new Disconnect(userInput.getArguments(), communicationApi);
                break;
            case PUT:
                recognizedCommand = new Put.Builder().arguments(userInput.getArguments())
                        .communicationApi(communicationApi)
                        .ringMetadataDeserializer(ringMetadataDeserializer).build();
                break;
            case GET:
                recognizedCommand = new Get.Builder().arguments(userInput.getArguments())
                        .communicationApi(communicationApi)
                        .ringMetadataDeserializer(ringMetadataDeserializer).build();
                break;
            case HELP:
                recognizedCommand = new Help(userInput.getArguments());
                break;
            case LOGLEVEL:
                recognizedCommand = new LogLevel(userInput.getArguments());
                break;
            case QUIT:
                recognizedCommand = new Quit(userInput.getArguments());
                break;
            default:
                LOGGER.info(StringUtils.join(" ", "Unrecognized command:", userCommand));
                recognizedCommand = new DefaultCommand(null);
                break;
        }
        return recognizedCommand;
    }
}
