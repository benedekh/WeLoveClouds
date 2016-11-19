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
    private static final Logger LOGGER = Logger.getLogger(Port.class);
    
    private ServerCLIConfigurationContext context;

    /**
     * @param arguments the {@link #PORT_INDEX} element of the array shall contain new port
     * @param context contains the server parameter configuration
     */
    public Port(String[] arguments, ServerCLIConfigurationContext context) {
        super(arguments);
        this.context = context;
    }

    @Override
    public void execute() throws ServerSideException {
        try {
            LOGGER.info("Executing port command.");
            int port = Integer.parseInt(arguments[PORT_INDEX]);
            context.setPort(port);

            String statusMessage = join(" ", "Latest port:", String.valueOf(port));
            userOutputWriter.writeLine(statusMessage);
            LOGGER.debug(statusMessage);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new ServerSideException(ex.getMessage(), ex);
        } finally {
            LOGGER.info("port command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validatePortArguments(arguments);
        return this;
    }

}
