package weloveclouds.ecs.models.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weloveclouds.commons.cli.utils.UserOutputWriter;

/**
 * Created by Benoit, Hunton on 2016-11-19.
 */
public abstract class AbstractCommand<T> implements ICommand {
    protected List<T> arguments;
    protected UserOutputWriter userOutputWriter = UserOutputWriter.getInstance();

    public AbstractCommand() {
        arguments = new ArrayList<>();
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

    public void addArguments(T[] arguments) {
        this.arguments.addAll(Arrays.asList(arguments));
    }

    public abstract String toString();
}
