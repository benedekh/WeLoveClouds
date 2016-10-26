package test.weloveclouds.client.utils;

import static org.fest.assertions.Assertions.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;


import java.net.UnknownHostException;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Created by Benoit on 2016-10-26.
 */
public class ArgumentsValidatorTest {
    private static final String INVALID_IP_ADDRESS = "darthvader";
    private static final String VALID_IP_ADDRESS = "131.159.52.2";
    private static final String VALID_NETWORK_PORT_NUMBER = "50000";
    private static final String INVALID_NETWORK_PORT_LOWER_LIMIT = "-1";
    private static final String INVALID_NETWORK_PORT_UPPER_LIMIT = "70000";
    private static final String[] NULL_COMMAND_ARGUMENTS = null;
    private static final String[] NOT_NULL_COMMAND_ARGUMENT = {"args"};
    private static final int SEND_MESSAGE_SIZZE_LIMIT_IN_BYTES = 128;

    @Before
    public void setUp() throws Exception {
    }

    @Test(expected = UnknownHostException.class)
    public void shouldThrowWhenValidatingInvalidIp() throws Exception {
        String[] connectCommandArguments = {INVALID_IP_ADDRESS, VALID_NETWORK_PORT_NUMBER};
        ArgumentsValidator
                .validateConnectArguments(connectCommandArguments, new ServerConnectionInfo
                        .ServerConnectionInfoBuilder()
                        .ipAddress(INVALID_IP_ADDRESS)
                        .port(Integer.parseInt(VALID_NETWORK_PORT_NUMBER))
                        .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenValidatingInvalidNetworkPortNumber() throws Exception {
        String[] connectCommandArguments = {VALID_IP_ADDRESS, INVALID_NETWORK_PORT_LOWER_LIMIT};
        boolean hasThrown = false;

        try {
            ArgumentsValidator
                    .validateConnectArguments(connectCommandArguments, new ServerConnectionInfo
                            .ServerConnectionInfoBuilder()
                            .ipAddress(VALID_IP_ADDRESS)
                            .port(Integer.parseInt(INVALID_NETWORK_PORT_LOWER_LIMIT))
                            .build());
        } catch (IllegalArgumentException e) {
            hasThrown = true;
            ArgumentsValidator
                    .validateConnectArguments(connectCommandArguments, new ServerConnectionInfo
                            .ServerConnectionInfoBuilder()
                            .ipAddress(VALID_IP_ADDRESS)
                            .port(Integer.parseInt(INVALID_NETWORK_PORT_UPPER_LIMIT))
                            .build());
        } finally {
            assertThat(hasThrown).isTrue();
        }
    }

    @Test(expected = UnknownHostException.class)
    public void shouldThrowIfConnectArgumentIsNull() throws Exception {
        String nullStringValue = null;
        Integer nullIntValue = null;

        ArgumentsValidator
                .validateConnectArguments(NULL_COMMAND_ARGUMENTS, new ServerConnectionInfo
                        .ServerConnectionInfoBuilder()
                        .ipAddress(nullStringValue)
                        .port(nullIntValue)
                        .build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfSendArgumentIsNull() {
        ArgumentsValidator.validateSendArguments(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfSendMessageIsLongerThan128Bytes() throws Exception {
        String sendMessage = "";
        String[] sendArgument = {StringUtils.leftPad(sendMessage,
                SEND_MESSAGE_SIZZE_LIMIT_IN_BYTES + 1, "a")};

        ArgumentsValidator.validateSendArguments(sendArgument);
    }

    @Test
    public void shouldNotThrowIfSendMessageIs128BytesOrLower() {
        String sendMessage = "";
        String[] sizeLimitMessage = {StringUtils.leftPad(sendMessage,
                SEND_MESSAGE_SIZZE_LIMIT_IN_BYTES, "a")};
        String[] validMessage = {StringUtils.leftPad(sendMessage,
                SEND_MESSAGE_SIZZE_LIMIT_IN_BYTES - 1, "a")};
        ArgumentsValidator.validateSendArguments(sizeLimitMessage);
        ArgumentsValidator.validateSendArguments(validMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfDisconnectArgumentsIsNotNullOrEmpty() throws Exception {
        ArgumentsValidator.validateDisconnectArguments(NOT_NULL_COMMAND_ARGUMENT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfQuitArgumentsIsNotNullOrEmpty() throws Exception {
        ArgumentsValidator.validateQuitArguments(NOT_NULL_COMMAND_ARGUMENT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfHelpArgumentIsNotNullOrEmpty() throws Exception {
        ArgumentsValidator.validateHelpArguments(NOT_NULL_COMMAND_ARGUMENT);
    }

    @Test
    public void shouldNotThrowIfDisconnectArgumentsIsNullOrEmpty() {
        ArgumentsValidator.validateDisconnectArguments(NULL_COMMAND_ARGUMENTS);
    }

    @Test
    public void shouldNotThrowIfQuitArgumentsIsNullOrEmpty() {
        ArgumentsValidator.validateQuitArguments(NULL_COMMAND_ARGUMENTS);
    }

    @Test
    public void shouldNotThrowIfHelpArgumentIsNullOrEmpty() {
        ArgumentsValidator.validateHelpArguments(NULL_COMMAND_ARGUMENTS);
    }
}