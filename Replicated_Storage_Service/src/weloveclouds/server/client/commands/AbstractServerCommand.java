package weloveclouds.server.client.commands;

import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.server.core.ServerCLIHandler;

/**
 * Represents a command that is received from the CLI and is executed by the
 * {@link ServerCLIHandler}.
 * 
 * @author Benedek
 */
public abstract class AbstractServerCommand implements ICommand {
    protected String[] arguments;
    protected UserOutputWriter userOutputWriter = UserOutputWriter.getInstance();

    /**
     * @param arguments the arguments of the command
     */
    public AbstractServerCommand(String[] arguments) {
        this.arguments = arguments;
    }
}
