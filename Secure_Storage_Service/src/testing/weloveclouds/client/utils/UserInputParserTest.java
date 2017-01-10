package testing.weloveclouds.client.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import weloveclouds.client.commands.ClientCommand;
import weloveclouds.client.core.ClientUserInputParser;
import weloveclouds.commons.cli.models.ParsedUserInput;
import weloveclouds.commons.cli.utils.AbstractUserInputParser;
import weloveclouds.commons.utils.StringUtils;

/**
 * Unit tests to validate the {@link AbstractUserInputParser}'s correct behavior.
 *
 * @author Benoit, Benedek, Hunton
 */
public class UserInputParserTest extends TestCase {
    private static final String PUT_COMMAND = "put";
    private static final String GET_COMMAND = "get";
    private static final String CONNECT_COMMAND = "connect";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String HELP_COMMAND = "help";
    private static final String QUIT_COMMAND = "quit";
    private static final String LOGLEVEL_COMMAND = "logLevel";
    private static final String INVALID_COMMAND = "lukeskywalker";
    private static final String SINGLE_ARGUMENTS = "testargs123:";
    private static final String MULTIPLE_ARGUMENTS = "args1 args2 args3";
    private static final String ARGUMENTS_DELIMITER = " ";
    private final int SINGLE_ARGUMENT = 1;
    private AbstractUserInputParser<ClientCommand> userInputParser;

    @Before
    public void setUp() {
        userInputParser = new ClientUserInputParser();
    }

    @Test
    public void testShouldParseValidCommandsWithoutArguments() {
        verifyValidCommandParsingWithoutArguments(PUT_COMMAND, ClientCommand.PUT);
        verifyValidCommandParsingWithoutArguments(GET_COMMAND, ClientCommand.GET);
        verifyValidCommandParsingWithoutArguments(CONNECT_COMMAND, ClientCommand.CONNECT);
        verifyValidCommandParsingWithoutArguments(DISCONNECT_COMMAND, ClientCommand.DISCONNECT);
        verifyValidCommandParsingWithoutArguments(HELP_COMMAND, ClientCommand.HELP);
        verifyValidCommandParsingWithoutArguments(QUIT_COMMAND, ClientCommand.QUIT);
        verifyValidCommandParsingWithoutArguments(LOGLEVEL_COMMAND, ClientCommand.LOGLEVEL);
        verifyValidCommandParsingWithoutArguments(INVALID_COMMAND, ClientCommand.DEFAULT);
    }

    @Test
    public void testShouldParseValidCommandsWithSingleArguments() {
        verifyValidCommandParsingWithSingleArgument(
                StringUtils.join(ARGUMENTS_DELIMITER, Arrays.asList(GET_COMMAND, SINGLE_ARGUMENTS)),
                ClientCommand.GET, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(CONNECT_COMMAND, SINGLE_ARGUMENTS)),
                ClientCommand.CONNECT, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(DISCONNECT_COMMAND, SINGLE_ARGUMENTS)),
                ClientCommand.DISCONNECT, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(HELP_COMMAND, SINGLE_ARGUMENTS)),
                ClientCommand.HELP, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(QUIT_COMMAND, SINGLE_ARGUMENTS)),
                ClientCommand.QUIT, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(LOGLEVEL_COMMAND, SINGLE_ARGUMENTS)),
                ClientCommand.LOGLEVEL, SINGLE_ARGUMENTS);
        verifyValidCommandParsingWithSingleArgument(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(INVALID_COMMAND, SINGLE_ARGUMENTS)),
                ClientCommand.DEFAULT, SINGLE_ARGUMENTS);
    }

    @Test
    public void testShouldParseValidCommandsWithMultipleArguments() {
        verifyValidCommandParsingWithMultipleArguments(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(PUT_COMMAND, MULTIPLE_ARGUMENTS)),
                ClientCommand.PUT, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(CONNECT_COMMAND, MULTIPLE_ARGUMENTS)),
                ClientCommand.CONNECT, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(DISCONNECT_COMMAND, MULTIPLE_ARGUMENTS)),
                ClientCommand.DISCONNECT, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(HELP_COMMAND, MULTIPLE_ARGUMENTS)),
                ClientCommand.HELP, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(QUIT_COMMAND, MULTIPLE_ARGUMENTS)),
                ClientCommand.QUIT, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(LOGLEVEL_COMMAND, MULTIPLE_ARGUMENTS)),
                ClientCommand.LOGLEVEL, MULTIPLE_ARGUMENTS);
        verifyValidCommandParsingWithMultipleArguments(
                StringUtils.join(ARGUMENTS_DELIMITER,
                        Arrays.asList(INVALID_COMMAND, MULTIPLE_ARGUMENTS)),
                ClientCommand.DEFAULT, MULTIPLE_ARGUMENTS);
    }

    private void verifyValidCommandParsingWithoutArguments(String userInput,
            ClientCommand expectedCommand) {
        ParsedUserInput<?> parsedUserInput = userInputParser.parse(userInput);
        assertThat(parsedUserInput.getCommand()).isEqualTo(expectedCommand);
        assertThat(parsedUserInput.getArguments()).isNullOrEmpty();
    }

    private void verifyValidCommandParsingWithSingleArgument(String userInput,
            ClientCommand expectedCommand, String argument) {
        ParsedUserInput<?> parsedUserInput = userInputParser.parse(userInput);
        assertThat(parsedUserInput.getCommand()).isEqualTo(expectedCommand);
        assertThat(parsedUserInput.getArguments().length).isEqualTo(SINGLE_ARGUMENT);
        assertThat(parsedUserInput.getArguments()).contains(argument);
    }

    private void verifyValidCommandParsingWithMultipleArguments(String userInput,
            ClientCommand expectedCommand, String arguments) {
        ParsedUserInput<?> parsedUserInput = userInputParser.parse(userInput);
        assertThat(parsedUserInput.getCommand()).isEqualTo(expectedCommand);
        assertThat(parsedUserInput.getArguments().length).isGreaterThan(SINGLE_ARGUMENT);
        assertThat(parsedUserInput.getArguments())
                .containsOnly(arguments.split(ARGUMENTS_DELIMITER));
    }
}
