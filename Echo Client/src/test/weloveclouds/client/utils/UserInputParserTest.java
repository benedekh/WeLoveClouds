package test.weloveclouds.client.utils;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;

import java.util.Arrays;

import weloveclouds.client.models.Command;
import weloveclouds.client.models.ParsedUserInput;
import weloveclouds.client.utils.StringJoiner;
import weloveclouds.client.utils.UserInputParser;

/**
 * @author Benoit
 */
public class UserInputParserTest {
    private static final String SEND_COMMAND = "send";
    private static final String CONNECT_COMMAND = "connect";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String HELP_COMMAND = "help";
    private static final String QUIT_COMMAND = "quit";
    private static final String LOGLEVEL_COMMAND = "loglevel";
    private static final String INVALID_COMMAND = "lukeskywalker";
    private static final String SINGLE_ARGUMENTS = "testargs123:";
    private static final String MULTIPLE_ARGUMENTS = "args1 args2 args3";
    private static final String ARGUMENTS_DELIMITER = " ";
    private final int SINGLE_ARGUMENT = 1;

    @Test
    public void shouldParseValidCommandsWithoutArguments() {
        verifyValidCommandParsingWithoutArguments(SEND_COMMAND, Command.SEND);
        verifyValidCommandParsingWithoutArguments(CONNECT_COMMAND, Command.CONNECT);
        verifyValidCommandParsingWithoutArguments(DISCONNECT_COMMAND, Command.DISCONNECT);
        verifyValidCommandParsingWithoutArguments(HELP_COMMAND, Command.HELP);
        verifyValidCommandParsingWithoutArguments(QUIT_COMMAND, Command.QUIT);
        verifyValidCommandParsingWithoutArguments(LOGLEVEL_COMMAND, Command.LOGLEVEL);
        verifyValidCommandParsingWithoutArguments(INVALID_COMMAND, Command.DEFAULT);
    }

    @Test
    public void shouldParseValidCommandsWithSingleArguments() {
        verifyValidCommandParsingWithSingleArgument(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                        .asList(SEND_COMMAND, SINGLE_ARGUMENTS)), Command.SEND, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(CONNECT_COMMAND, SINGLE_ARGUMENTS)), Command.CONNECT, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(DISCONNECT_COMMAND, SINGLE_ARGUMENTS)), Command.DISCONNECT, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(HELP_COMMAND, SINGLE_ARGUMENTS)), Command.HELP, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(QUIT_COMMAND, SINGLE_ARGUMENTS)), Command.QUIT, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(LOGLEVEL_COMMAND, SINGLE_ARGUMENTS)), Command.LOGLEVEL, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(INVALID_COMMAND, SINGLE_ARGUMENTS)), Command.DEFAULT, SINGLE_ARGUMENTS);
    }

    @Test
    public void shouldParseValidCommandsWithMultipleArguments() {
        verifyValidCommandParsingWithMultipleArguments(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(SEND_COMMAND, MULTIPLE_ARGUMENTS)), Command.SEND, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(CONNECT_COMMAND, MULTIPLE_ARGUMENTS)), Command.CONNECT, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(DISCONNECT_COMMAND, MULTIPLE_ARGUMENTS)), Command.DISCONNECT, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(HELP_COMMAND, MULTIPLE_ARGUMENTS)), Command.HELP, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(QUIT_COMMAND, MULTIPLE_ARGUMENTS)), Command.QUIT, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(LOGLEVEL_COMMAND, MULTIPLE_ARGUMENTS)), Command.LOGLEVEL, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(StringJoiner.join(ARGUMENTS_DELIMITER, Arrays
                .asList(INVALID_COMMAND, MULTIPLE_ARGUMENTS)), Command.DEFAULT, MULTIPLE_ARGUMENTS);
    }

    private void verifyValidCommandParsingWithoutArguments(String userInput, Command expectedCommand) {
        ParsedUserInput parsedUserInput = UserInputParser.parse(userInput);
        assertThat(parsedUserInput.getCommand()).isEqualTo(expectedCommand);
        assertThat(parsedUserInput.getArguments()).isNullOrEmpty();
    }

    private void verifyValidCommandParsingWithSingleArgument(String userInput, Command
            expectedCommand, String argument) {
        ParsedUserInput parsedUserInput = UserInputParser.parse(userInput);
        assertThat(parsedUserInput.getCommand()).isEqualTo(expectedCommand);
        assertThat(parsedUserInput.getArguments().length).isEqualTo(SINGLE_ARGUMENT);
        assertThat(parsedUserInput.getArguments()).contains(argument);
    }

    private void verifyValidCommandParsingWithMultipleArguments(String userInput, Command
            expectedCommand, String arguments){
        ParsedUserInput parsedUserInput = UserInputParser.parse(userInput);
        assertThat(parsedUserInput.getCommand()).isEqualTo(expectedCommand);
        assertThat(parsedUserInput.getArguments().length).isGreaterThan(SINGLE_ARGUMENT);
        assertThat(parsedUserInput.getArguments()).containsOnly(arguments.split(ARGUMENTS_DELIMITER));
    }
}
