package testing.weloveclouds.client.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

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
    private static final int KEY_SIZE_LIMIT_IN_BYTES = 20;
    private static final int VALUE_SIZE_LIMIT_IN_BYTES = 120 * 1000;

    @Before
    public void setUp() throws Exception {}

    @Test(expected = UnknownHostException.class)
    public void shouldThrowWhenValidatingInvalidIp() throws Exception {
        String[] connectCommandArguments = {INVALID_IP_ADDRESS, VALID_NETWORK_PORT_NUMBER};
        ArgumentsValidator.validateConnectArguments(connectCommandArguments,
                new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress(INVALID_IP_ADDRESS)
                        .port(Integer.parseInt(VALID_NETWORK_PORT_NUMBER)).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenValidatingInvalidNetworkPortNumber() throws Exception {
        String[] connectCommandArguments = {VALID_IP_ADDRESS, INVALID_NETWORK_PORT_LOWER_LIMIT};
        boolean hasThrown = false;

        try {
            ArgumentsValidator.validateConnectArguments(connectCommandArguments,
                    new ServerConnectionInfo.ServerConnectionInfoBuilder()
                            .ipAddress(VALID_IP_ADDRESS)
                            .port(Integer.parseInt(INVALID_NETWORK_PORT_LOWER_LIMIT)).build());
        } catch (IllegalArgumentException e) {
            hasThrown = true;
            ArgumentsValidator.validateConnectArguments(connectCommandArguments,
                    new ServerConnectionInfo.ServerConnectionInfoBuilder()
                            .ipAddress(VALID_IP_ADDRESS)
                            .port(Integer.parseInt(INVALID_NETWORK_PORT_UPPER_LIMIT)).build());
        } finally {
            assertThat(hasThrown).isTrue();
        }
    }

    @Test(expected = UnknownHostException.class)
    public void shouldThrowIfConnectArgumentIsNull() throws Exception {
        String nullStringValue = null;
        Integer nullIntValue = null;

        ArgumentsValidator.validateConnectArguments(NULL_COMMAND_ARGUMENTS,
                new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress(nullStringValue)
                        .port(nullIntValue).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfPutArgumentIsNull() {
        ArgumentsValidator.validatePutArguments(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfGetArgumentIsNull() {
        ArgumentsValidator.validateGetArguments(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfGetKeyIsLongerThan20Bytes() throws Exception {
        String sendMessage = "";
        String[] sendArgument =
                {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES + 1, "a")};

        ArgumentsValidator.validateGetArguments(sendArgument);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfPutKeyIsLongerThan20Bytes() throws Exception {
        String sendMessage = "";
        String[] sendArgument =
                {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES + 1, "a")};

        ArgumentsValidator.validatePutArguments(sendArgument);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfPutValueIsLongerThan120kBytes() throws Exception {
        String sendMessage = "";
        String[] sendArgument =
                {StringUtils.leftPad(sendMessage, VALUE_SIZE_LIMIT_IN_BYTES + 1, "a")};

        ArgumentsValidator.validatePutArguments(sendArgument);
    }

    @Test
    public void shouldNotThrowIfGetKeyIs20BytesOrLower() {
        String sendMessage = "";
        String[] sizeLimitMessage =
                {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES, "a")};
        String[] validMessage =
                {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES - 1, "a")};
        ArgumentsValidator.validateGetArguments(sizeLimitMessage);
        ArgumentsValidator.validateGetArguments(validMessage);
    }

    @Test
    public void shouldNotThrowIfPutKeyIs20BytesOrLower() {
        String sendMessage = "";
        String[] sizeLimitMessage =
                {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES, "a")};
        String[] validMessage =
                {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES - 1, "a")};
        ArgumentsValidator.validatePutArguments(sizeLimitMessage);
        ArgumentsValidator.validatePutArguments(validMessage);
    }

    @Test
    public void shouldNotThrowIfPutValueIs20BytesOrLower() {
        String sendMessage = "";
        String[] sizeLimitMessage =
                {StringUtils.leftPad(sendMessage, VALUE_SIZE_LIMIT_IN_BYTES, "a")};
        String[] validMessage =
                {StringUtils.leftPad(sendMessage, VALUE_SIZE_LIMIT_IN_BYTES - 1, "a")};
        ArgumentsValidator.validatePutArguments(sizeLimitMessage);
        ArgumentsValidator.validatePutArguments(validMessage);
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
