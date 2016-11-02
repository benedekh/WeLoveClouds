package weloveclouds.server.models.commands;

import weloveclouds.cli.utils.UserOutputWriter;

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
