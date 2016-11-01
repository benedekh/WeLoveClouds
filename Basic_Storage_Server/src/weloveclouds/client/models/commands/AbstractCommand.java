package weloveclouds.client.models.commands;

import weloveclouds.client.utils.UserOutputWriter;

/**
 * Abstract common class for processing commands. It stores the {@link #arguments} that a specific
 * command may have.
 *
 * @author Benoit
 */
public abstract class AbstractCommand implements ICommand {
    protected String[] arguments;
    protected UserOutputWriter userOutputWriter = UserOutputWriter.getInstance();

    /**
     * @param arguments the arguments of the command
     */
    public AbstractCommand(String[] arguments) {
        this.arguments = arguments;
    }
}
