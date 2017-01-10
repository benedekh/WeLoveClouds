package testing.weloveclouds.client.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import junit.framework.TestCase;
import weloveclouds.client.commands.utils.ArgumentsValidator;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Unit tests to validate the {@link ArgumentsValidator} correct behavior.
 * 
 * @author Benoit, Benedek, Hunton
 */
public class ArgumentsValidatorTest extends TestCase {
    private static final String INVALID_IP_ADDRESS = "darthvader";
    private static final String VALID_IP_ADDRESS = "131.159.52.2";
    private static final String VALID_NETWORK_PORT_NUMBER = "50000";
    private static final String INVALID_NETWORK_PORT_LOWER_LIMIT = "-1";
    private static final String INVALID_NETWORK_PORT_UPPER_LIMIT = "70000";
    private static final String[] NULL_COMMAND_ARGUMENTS = null;
    private static final String[] NOT_NULL_COMMAND_ARGUMENT = {"args"};
    private static final int KEY_SIZE_LIMIT_IN_BYTES = 20;
    private static final int VALUE_SIZE_LIMIT_IN_BYTES = 120 * 1000;

    @Test
    public void testShouldThrowWhenValidatingInvalidIp() {
        Exception ex = null;
        try {
            String[] connectCommandArguments = {INVALID_IP_ADDRESS, VALID_NETWORK_PORT_NUMBER};
            ArgumentsValidator.validateConnectArguments(connectCommandArguments,
                    new ServerConnectionInfo.Builder().ipAddress(INVALID_IP_ADDRESS)
                            .port(Integer.parseInt(VALID_NETWORK_PORT_NUMBER)).build());
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof UnknownHostException);
        }
    }

    @Test
    public void testShouldThrowWhenValidatingInvalidNetworkPortNumber() {
        Exception ex = null;
        try {
            String[] connectCommandArguments = {VALID_IP_ADDRESS, INVALID_NETWORK_PORT_LOWER_LIMIT};
            boolean hasThrown = false;

            try {
                ArgumentsValidator.validateConnectArguments(connectCommandArguments,
                        new ServerConnectionInfo.Builder().ipAddress(VALID_IP_ADDRESS)
                                .port(Integer.parseInt(INVALID_NETWORK_PORT_LOWER_LIMIT)).build());
            } catch (IllegalArgumentException e) {
                hasThrown = true;
                ArgumentsValidator.validateConnectArguments(connectCommandArguments,
                        new ServerConnectionInfo.Builder().ipAddress(VALID_IP_ADDRESS)
                                .port(Integer.parseInt(INVALID_NETWORK_PORT_UPPER_LIMIT)).build());
            } finally {
                assertThat(hasThrown).isTrue();
            }
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof IllegalArgumentException);
        }


    }

    @SuppressWarnings("null")
    @Test
    public void testShouldThrowIfConnectArgumentIsNull() {
        Exception ex = null;
        try {
            String nullStringValue = null;
            Integer nullIntValue = null;

            ArgumentsValidator.validateConnectArguments(NULL_COMMAND_ARGUMENTS,
                    new ServerConnectionInfo.Builder().ipAddress(nullStringValue).port(nullIntValue)
                            .build());
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof UnknownHostException);
        }
    }

    @Test
    public void testShouldThrowIfPutArgumentIsNull() {
        Exception ex = null;
        try {
            ArgumentsValidator.validatePutArguments(null);
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testShouldThrowIfGetArgumentIsNull() {
        Exception ex = null;
        try {
            ArgumentsValidator.validateGetArguments(null);
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testShouldThrowIfGetKeyIsLongerThan20Bytes() throws Exception {
        Exception ex = null;
        try {
            String sendMessage = "";
            String[] sendArgument =
                    {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES + 1, "a")};

            ArgumentsValidator.validateGetArguments(sendArgument);
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testShouldThrowIfPutKeyIsLongerThan20Bytes() throws Exception {
        Exception ex = null;
        try {
            String sendMessage = "";
            String[] sendArgument =
                    {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES + 1, "a")};

            ArgumentsValidator.validatePutArguments(sendArgument);
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testShouldThrowIfPutValueIsLongerThan120kBytes() {
        Exception ex = null;
        try {
            String sendMessage = "";
            String[] sendArgument =
                    {StringUtils.leftPad(sendMessage, VALUE_SIZE_LIMIT_IN_BYTES + 1, "a")};

            ArgumentsValidator.validatePutArguments(sendArgument);
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testShouldNotThrowIfGetKeyIs20BytesOrLower() {
        String sendMessage = "";
        String[] sizeLimitMessage =
                {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES, "a")};
        String[] validMessage =
                {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES - 1, "a")};
        ArgumentsValidator.validateGetArguments(sizeLimitMessage);
        ArgumentsValidator.validateGetArguments(validMessage);
    }

    @Test
    public void testShouldNotThrowIfPutKeyIs20BytesOrLower() {
        String sendMessage = "";
        String[] sizeLimitMessage =
                {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES, "a")};
        String[] validMessage =
                {StringUtils.leftPad(sendMessage, KEY_SIZE_LIMIT_IN_BYTES - 1, "a")};
        ArgumentsValidator.validatePutArguments(sizeLimitMessage);
        ArgumentsValidator.validatePutArguments(validMessage);
    }

    @Test
    public void testShouldNotThrowIfPutValueIs120kBytesOrLower() {
        String sendMessage = "";
        String[] sizeLimitMessage =
                {StringUtils.leftPad(sendMessage, VALUE_SIZE_LIMIT_IN_BYTES, "a")};
        String[] validMessage =
                {StringUtils.leftPad(sendMessage, VALUE_SIZE_LIMIT_IN_BYTES - 1, "a")};

        ArrayDeque<String> limitDeque = new ArrayDeque<String>(Arrays.asList(sizeLimitMessage));
        limitDeque.addFirst(sendMessage);
        String[] limitArguments = limitDeque.toArray(new String[limitDeque.size()]);


        ArrayDeque<String> validDeque = new ArrayDeque<String>(Arrays.asList(validMessage));
        validDeque.addFirst(sendMessage);
        String[] validArguments = validDeque.toArray(new String[validDeque.size()]);

        ArgumentsValidator.validatePutArguments(limitArguments);
        ArgumentsValidator.validatePutArguments(validArguments);
    }

    @Test
    public void testShouldThrowIfDisconnectArgumentsIsNotNullOrEmpty() {
        Exception ex = null;
        try {
            ArgumentsValidator.validateDisconnectArguments(NOT_NULL_COMMAND_ARGUMENT);
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testShouldThrowIfQuitArgumentsIsNotNullOrEmpty() {
        Exception ex = null;
        try {
            ArgumentsValidator.validateQuitArguments(NOT_NULL_COMMAND_ARGUMENT);
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testShouldThrowIfHelpArgumentIsNotNullOrEmpty() {
        Exception ex = null;
        try {
            ArgumentsValidator.validateHelpArguments(NOT_NULL_COMMAND_ARGUMENT);
        } catch (Exception e) {
            ex = e;
        } finally {
            assertTrue(ex instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testShouldNotThrowIfDisconnectArgumentsIsNullOrEmpty() {
        ArgumentsValidator.validateDisconnectArguments(NULL_COMMAND_ARGUMENTS);
    }

    @Test
    public void testShouldNotThrowIfQuitArgumentsIsNullOrEmpty() {
        ArgumentsValidator.validateQuitArguments(NULL_COMMAND_ARGUMENTS);
    }

    @Test
    public void testShouldNotThrowIfHelpArgumentIsNullOrEmpty() {
        ArgumentsValidator.validateHelpArguments(NULL_COMMAND_ARGUMENTS);
    }
}
