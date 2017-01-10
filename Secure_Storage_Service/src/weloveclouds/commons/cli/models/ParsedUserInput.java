package weloveclouds.commons.cli.models;

/**
 * Represents a user input which is split into a {@link #command} and its {@link #arguments}.
 *
 * @author Benoit, Benedek
 */
public class ParsedUserInput<T> {
    private T command;
    private String[] arguments = {};

    public ParsedUserInput(T command) {
        this.command = command;
    }

    public T getCommand() {
        return this.command;
    }

    public String[] getArguments() {
        return arguments;
    }

    public ParsedUserInput<?> withArguments(String[] arguments) {
        this.arguments = arguments;
        return this;
    }
}
