package weloveclouds.server.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.cli.models.ParsedUserInput;
import weloveclouds.client.models.commands.Connect;
import weloveclouds.server.models.ServerConfigurationContext;

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
        ServerCommand userCommand = userInput.getServerCommand();
        String[] arguments = userInput.getArguments();
        ICommand recognizedCommand = null;

        switch (userCommand) {
            case CACHESIZE:
                recognizedCommand = new CacheSize(arguments, context);
                break;
            case HELP:
                recognizedCommand = new Help(arguments);
                break;
            case LOGLEVEL:
                recognizedCommand = new LogLevel(arguments);
                break;
            case PORT:
                recognizedCommand = new Port(arguments, context);
                break;
            case START:
                recognizedCommand = new Start(arguments, context);
                break;
            case STORAGEPATH:
                recognizedCommand = new StoragePath(arguments, context);
                break;
            case STRATEGY:
                recognizedCommand = new Strategy(arguments, context);
                break;
            case QUIT:
                recognizedCommand = new Quit(arguments);
                break;
            default:
                logger.info(join(" ", "Unrecognized command:", userCommand.toString()));
                recognizedCommand = new DefaultCommand(null);
                break;
        }
        return recognizedCommand;
    }
}

