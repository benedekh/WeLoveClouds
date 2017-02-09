package weloveclouds.server.client.commands;

import org.apache.log4j.Logger;

import weloveclouds.commons.cli.models.ParsedUserInput;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.configuration.models.KVServerCLIContext;
import weloveclouds.server.core.ServerFactory;
import weloveclouds.server.services.datastore.DataAccessServiceFactory;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different commands. It
 * handles several commands (see {@link ServerCommand} for the possible commands) by dispatching the
 * command to its respective handler.
 *
 * @author Benedek, Hunton
 */
public class ServerCommandFactory {

    private static final Logger LOGGER = Logger.getLogger(ServerCommandFactory.class);

    private ServerFactory serverFactory;
    private DataAccessServiceFactory dataAccessServiceFactory;
    private KVServerCLIContext context;

    public ServerCommandFactory(DataAccessServiceFactory dataAccessServiceFactory,
            ServerFactory serverFactory) {
        this.dataAccessServiceFactory = dataAccessServiceFactory;
        this.serverFactory = serverFactory;
        this.context = new KVServerCLIContext();
    }

    /**
     * Dispatches the command that is stored in the userInput to its respective handler, which
     * processes it.
     *
     * @param userInput which contains the command and its arguments
     * @return the type of the recognized command
     */
    public ICommand createCommandFromUserInput(ParsedUserInput<ServerCommand> userInput) {
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
            case CLIENT_PORT:
                recognizedCommand = new ClientPort(arguments, context);
                break;
            case SERVER_PORT:
                recognizedCommand = new ServerPort(arguments, context);
                break;
            case ECS_PORT:
                recognizedCommand = new EcsPort(arguments, context);
                break;
            case START:
                recognizedCommand =
                        new Start.Builder().arguments(arguments).serverFactory(serverFactory)
                                .dataAccessServiceFactory(dataAccessServiceFactory)
                                .serverCLIContext(context).build();
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
                LOGGER.info(StringUtils.join(" ", "Unrecognized command:", userCommand));
                recognizedCommand = new DefaultCommand(null);
                break;
        }
        return recognizedCommand;
    }
}

