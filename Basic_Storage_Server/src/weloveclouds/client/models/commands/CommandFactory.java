package weloveclouds.client.models.commands;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.client.models.Command;
import weloveclouds.client.models.ParsedUserInput;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.v1.IKVCommunicationApi;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different commands. It
 * handles several commands (see {@link Command} for the possible commands) by dispatching the
 * command to its respective handler.
 * 
 * @author Benoit
 */
public class CommandFactory {
    private IKVCommunicationApi communicationApi;
    private Logger logger;

    /**
     * @param communicationApi an instance for the communication module for those commands which
     *        need to communicate via the network
     */
    public CommandFactory(IKVCommunicationApi communicationApi) {
        this.communicationApi = communicationApi;
        this.logger = Logger.getLogger(getClass());
    }

    /**
     * Dispatches the command that is stored in the userInput to its respective handler, which
     * processes it.
     * 
     * @param userInput which contains the command and its arguments
     * @return the type of the recognized command
     * @throws UnknownHostException see {@link Connect}
     */
    public ICommand createCommandFromUserInput(ParsedUserInput userInput)
            throws UnknownHostException {
        ICommand recognizedCommand = null;
        Command userCommand = userInput.getCommand();

        switch (userCommand) {
            case CONNECT:
                recognizedCommand = new Connect(userInput.getArguments(), communicationApi);
                break;
            case DISCONNECT:
                recognizedCommand = new Disconnect(userInput.getArguments(), communicationApi);
                break;
            case PUT:
                recognizedCommand = new Put(userInput.getArguments(), communicationApi);
                break;
            case GET:
                recognizedCommand = new Get(userInput.getArguments(), communicationApi);
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
                logger.info(
                        CustomStringJoiner.join(" ", "Unrecognized command:", userCommand.toString()));
                recognizedCommand = new DefaultCommand(null);
                break;
        }
        return recognizedCommand;
    }
}
