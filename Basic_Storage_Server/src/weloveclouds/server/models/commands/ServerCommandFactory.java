package weloveclouds.server.models.commands;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.client.models.ParsedUserInput;
import weloveclouds.client.models.commands.Connect;
import weloveclouds.server.models.ServerConfigurationContext;

import static weloveclouds.client.utils.CustomStringJoiner.join;

public class ServerCommandFactory {
    private ServerConfigurationContext context;
    private Logger logger;

    public ServerCommandFactory() {
        this.context = new ServerConfigurationContext();
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
        ServerCommand userCommand = userInput.getServerCommand();

        switch (userCommand) {
            case PORT:
                recognizedCommand = new Port(userInput.getArguments(), context);
                break;
            case LOGLEVEL:
                recognizedCommand = new LogLevel(userInput.getArguments());
                break;
            case STRATEGY:
                recognizedCommand = new Strategy(userInput.getArguments(), context);
                break;
            case QUIT:
                recognizedCommand = new Quit(userInput.getArguments());
                break;
            case START:
                recognizedCommand = new Start(userInput.getArguments(), context);
                break;
            default:
                logger.info(join(" ", "Unrecognized command:", userCommand.toString()));
                recognizedCommand = new DefaultCommand(null);
                break;
        }
        return recognizedCommand;
    }
}

