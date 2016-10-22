package weloveclouds.client.utils;

import weloveclouds.client.models.UserInput;
import weloveclouds.communication.models.Command;
import weloveclouds.communication.models.Request;

/**
 * @author Benoit
 */
public class UserInputToApiRequestConverterV1 implements UserInputConverter<Request> {
  public Request convert(UserInput userInput) {
    try {
      return new Request.RequestBuilder()
          .command(Command.valueOf(userInput.getCommand().toUpperCase()))
          .payload(userInput.getPayload()).build();
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
