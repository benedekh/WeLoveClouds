package weloveclouds.server.client.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.client.commands.utils.ArgumentsValidator;
import weloveclouds.server.configuration.models.KVServerCLIContext;

/**
 * The port on which the server is going to listen to other KVServers.
 *
 * @author Benedek, Hunton
 */
public class ServerPort extends AbstractServerCommand {

    private static final int PORT_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(ClientPort.class);

    private KVServerCLIContext context;

    /**
     * @param arguments the {@value #PORT_INDEX} element of the array shall contain new port
     * @param context contains the server parameter configuration
     */
    public ServerPort(String[] arguments, KVServerCLIContext context) {
        super(arguments);
        this.context = context;
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            LOGGER.info("Executing server port command.");
            int port = Integer.parseInt(arguments[PORT_INDEX]);
            context.setServerPort(port);

            String statusMessage = StringUtils.join(" ", "Latest port:", port);
            userOutputWriter.writeLine(statusMessage);
            LOGGER.debug(statusMessage);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("server port command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validatePortArguments(arguments);
        return this;
    }

}
