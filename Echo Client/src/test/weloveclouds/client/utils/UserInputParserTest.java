package test.weloveclouds.client.utils;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;

import weloveclouds.client.models.Command;
import weloveclouds.client.models.ParsedUserInput;
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

    @Test void shouldParseValidCommandsWithSingleArguments(){
        verifyValidCommandParsingWithoutArguments(SEND_COMMAND, Command.SEND);
        verifyValidCommandParsingWithoutArguments(CONNECT_COMMAND, Command.CONNECT);
        verifyValidCommandParsingWithoutArguments(DISCONNECT_COMMAND, Command.DISCONNECT);
        verifyValidCommandParsingWithoutArguments(HELP_COMMAND, Command.HELP);
        verifyValidCommandParsingWithoutArguments(QUIT_COMMAND, Command.QUIT);
        verifyValidCommandParsingWithoutArguments(LOGLEVEL_COMMAND, Command.LOGLEVEL);
        verifyValidCommandParsingWithoutArguments(INVALID_COMMAND, Command.DEFAULT);
    }

    private void verifyValidCommandParsingWithoutArguments(String userInput, Command expectedCommand){
        ParsedUserInput parsedUserInput = UserInputParser.parse(userInput);
        assertThat(parsedUserInput.getCommand()).isEqualTo(expectedCommand);
        assertThat(parsedUserInput.getArguments()).isNullOrEmpty();
    }

    private void verifyValidCommandParsingWithSingleArguments(String userInput, Command
            expectedCommand, String argument){
        final int SINGLE_ARGUMENT = 1;
        ParsedUserInput parsedUserInput = UserInputParser.parse(userInput);
        assertThat(parsedUserInput.getCommand()).isEqualTo(expectedCommand);
        assertThat(parsedUserInput.getArguments().length).isEqualTo(SINGLE_ARGUMENT);
        assertThat(parsedUserInput.getArguments()).contains(argument);
    }
}
