package weloveclouds.client.models.commands;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.client.models.Command;
import weloveclouds.client.models.ParsedUserInput;
import weloveclouds.client.utils.StringJoiner;
import weloveclouds.communication.api.ICommunicationApi;

/**
 * Created by Benoit on 2016-10-25.
 */
public class CommandFactory {
    private ICommunicationApi communicationApi;
    private Logger logger;

    public CommandFactory(ICommunicationApi communicationApi) {
        this.communicationApi = communicationApi;
        this.logger = Logger.getLogger(getClass());
    }

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
            case SEND:
                recognizedCommand = new Send(userInput.getArguments(), communicationApi);
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
                        StringJoiner.join(" ", "Unrecognized command:", userCommand.toString()));
                recognizedCommand = new DefaultCommand(null);
                break;
        }
        return recognizedCommand;
    }
}
