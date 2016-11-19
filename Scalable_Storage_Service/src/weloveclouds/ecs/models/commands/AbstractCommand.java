package weloveclouds.ecs.models.commands;

import weloveclouds.cli.utils.UserOutputWriter;
import weloveclouds.ecs.models.commands.ICommand;


/**
 * Abstract common class for processing commands. It stores the {@link #arguments} that a specific
 * command may have.
 *
 * @author Benoit, hb added it to the ecs package
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
