package weloveclouds.client.utils;

import weloveclouds.client.models.UserInput;
import weloveclouds.communication.models.Command;
import weloveclouds.communication.models.Request;

import org.apache.log4j.*;

/**
 * @author Benoit
 */
public class UserInputToApiRequestConverterV1 implements UserInputConverter<Request> {
  public Request convert(UserInput userInput) {
    try {
      return new Request.RequestBuilder()
          .command(Command.valueOf(userInput.getCommand().toUpperCase()))
          .payload(userInput.getArgument()).build();
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
