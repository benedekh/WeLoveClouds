package test.weloveclouds.client.utils;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.google.common.base.Joiner;

import weloveclouds.client.models.UserInput;
import weloveclouds.client.utils.UserInputParser;

/**
 * @author Benoit
 */
public class UserInputParserTest {
  private static final String SEND_COMMAND_CAPITALIZE = "SEND";
  private static final String SEND_COMMAND = "send";
  private static final String PAYLOAD = "An awesome Payload";
  private static final String API_VALID_USER_INPUT_NON_CAPITAL_LETTER =
      Joiner.on(" ").join(SEND_COMMAND, PAYLOAD);
  private static final String API_VALID_USER_INPUT_CAPITAL_LETTER =
      Joiner.on(" ").join(SEND_COMMAND_CAPITALIZE, PAYLOAD);

  @Test
  public void shouldCreateARequestOnValidUserInput() throws Exception {
    UserInput userRequest = UserInputParser.parse(API_VALID_USER_INPUT_NON_CAPITAL_LETTER);
    assertThat(userRequest.getCommand()).isEqualTo(SEND_COMMAND);
    assertThat(userRequest.getArgument()).isEqualTo(PAYLOAD);
  }

}
