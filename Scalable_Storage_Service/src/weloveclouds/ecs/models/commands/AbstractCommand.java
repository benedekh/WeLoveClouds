package weloveclouds.ecs.models.commands;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.cli.utils.UserOutputWriter;

/**
 * Created by Benoit on 2016-11-19.
 */
public abstract class AbstractCommand<T> implements ICommand {
    protected List<T> arguments;
    protected UserOutputWriter userOutputWriter = UserOutputWriter.getInstance();

    public AbstractCommand() {
        arguments = new ArrayList<T>();
    }

    public AbstractCommand(List<T> arguments) {
        this.arguments.addAll(arguments);
    }

    public void addArgument(T argument) {
        arguments.add(argument);
    }

    public void addArguments(List<T> arguments) {
        this.arguments.addAll(arguments);
    }

    public abstract String toString();
}
