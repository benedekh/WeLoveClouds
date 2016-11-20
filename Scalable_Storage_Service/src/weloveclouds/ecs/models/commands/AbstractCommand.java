package weloveclouds.ecs.models.commands;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.cli.utils.UserOutputWriter;

/**
 * Created by Benoit on 2016-11-19.
 */
public abstract class AbstractCommand<T1> implements ICommand {
    protected List<T1> arguments;
    protected UserOutputWriter userOutputWriter = UserOutputWriter.getInstance();

    public AbstractCommand() {
        arguments = new ArrayList<T1>();
    }

    public AbstractCommand(List<T1> arguments) {
        this.arguments.addAll(arguments);
    }

    public void addArgument(T1 argument) {
        arguments.add(argument);
    }

    public abstract String toString();
}
