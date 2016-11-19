package weloveclouds.cli.models;

import weloveclouds.client.models.commands.Command;
import weloveclouds.server.models.commands.ServerCommand;

/**
 * Represents a user input which is split into a {@link #command} and its {@link #arguments}.
 *
 * @author Benoit, Benedek
 */
public class ParsedUserInput {
    private String command;
    private String[] arguments = {};

    protected ParsedUserInput(Builder builder) {
        this.command = builder.command;
        this.arguments = builder.argument;
    }

    public Command getCommand() {
        return Command.createCommandFromString(command);
    }

    public ServerCommand getServerCommand() {
        return ServerCommand.createCommandFromString(command);
    }

    public String[] getArguments() {
        return arguments;
    }

    /**
     * Builder pattern for creating a {@link ParsedUserInput} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private String command;
        private String[] argument;

        public Builder command(String command) {
            this.command = command;
            return this;
        }

        public Builder arguments(String[] argument) {
            this.argument = argument;
            return this;
        }

        public ParsedUserInput build() {
            return new ParsedUserInput(this);
        }
    }
}
