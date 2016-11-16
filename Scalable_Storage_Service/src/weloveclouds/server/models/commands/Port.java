package weloveclouds.server.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.server.models.ServerCLIConfigurationContext;
import weloveclouds.server.models.exceptions.ServerSideException;
import weloveclouds.server.utils.ArgumentsValidator;

/**
 * The port on which the server is going to listen for the client.
 * 
 * @author Benedek
 */
public class Port extends AbstractServerCommand {

    private static final int PORT_INDEX = 0;

    private ServerCLIConfigurationContext context;
    private Logger logger;

    /**
     * @param arguments the {@link #PORT_INDEX} element of the array shall contain new port
     * @param context contains the server parameter configuration
     */
    public Port(String[] arguments, ServerCLIConfigurationContext context) {
        super(arguments);
        this.context = context;
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            logger.info("Executing port command.");
            int port = Integer.parseInt(arguments[PORT_INDEX]);
            context.setPort(port);

            String statusMessage = join(" ", "Latest port:", String.valueOf(port));
            userOutputWriter.writeLine(statusMessage);
            logger.debug(statusMessage);
        } catch (IOException ex) {
            logger.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            logger.info("port command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validatePortArguments(arguments);
        return this;
    }

}
