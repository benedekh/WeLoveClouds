package test.java.com.weloveclouds.client.utils;

import com.google.common.base.Joiner;

import org.junit.Test;

import main.java.com.weloveclouds.client.models.UserInput;
import main.java.com.weloveclouds.client.utils.UserInputParser;

import static org.fest.assertions.Assertions.*;

/**
 * Created by Benoit on 2016-10-21.
 */
public class UserInputParserTest {
    private static final String SEND_COMMAND_CAPITALIZE = "SEND";
    private static final String SEND_COMMAND = "send";
    private static final String PAYLOAD = "An awesome Payload";
    private static final String API_VALID_USER_INPUT_NON_CAPITAL_LETTER = Joiner.on(" ")
            .join(SEND_COMMAND,PAYLOAD);
    private static final String API_VALID_USER_INPUT_CAPITAL_LETTER = Joiner.on(" ")
            .join(SEND_COMMAND_CAPITALIZE, PAYLOAD);

    @Test
    public void shouldCreateARequestOnValidUserInput() throws Exception {
        UserInput userRequest = UserInputParser.parse(API_VALID_USER_INPUT_NON_CAPITAL_LETTER);
        assertThat(userRequest.getCommand()).isEqualTo(SEND_COMMAND);
        assertThat(userRequest.getPayload()).isEqualTo(PAYLOAD);
    }

}