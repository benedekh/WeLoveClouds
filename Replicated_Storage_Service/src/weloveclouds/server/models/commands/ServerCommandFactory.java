package weloveclouds.server.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.commons.cli.models.ParsedUserInput;
import weloveclouds.client.models.commands.Connect;
import weloveclouds.server.models.configuration.KVServerCLIContext;
import weloveclouds.server.services.DataAccessServiceFactory;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different commands. It
 * handles several commands (see {@link ServerCommand} for the possible commands) by dispatching the
 * command to its respective handler.
 *
 * @author Benedek
 */
public class ServerCommandFactory {

    private static final Logger LOGGER = Logger.getLogger(ServerCommandFactory.class);

    private DataAccessServiceFactory dataAccessServiceFactory;
    private KVServerCLIContext context;

    public ServerCommandFactory(DataAccessServiceFactory dataAccessServiceFactory) {
        this.dataAccessServiceFactory = dataAccessServiceFactory;
        this.context = new KVServerCLIContext();
    }

    /**
     * Dispatches the command that is stored in the userInput to its respective handler, which
     * processes it.
     *
     * @param userInput which contains the command and its arguments
     * @return the type of the recognized command
     * @throws UnknownHostException see {@link Connect}
     */
    public ICommand createCommandFromUserInput(ParsedUserInput<ServerCommand> userInput)
            throws UnknownHostException {
        ServerCommand userCommand = userInput.getCommand();
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
                recognizedCommand = new Start(arguments, dataAccessServiceFactory, context);
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
                LOGGER.info(join(" ", "Unrecognized command:", userCommand.toString()));
                recognizedCommand = new DefaultCommand(null);
                break;
        }
        return recognizedCommand;
    }
}

