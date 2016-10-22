package weloveclouds.communication.models;

import com.google.common.base.Joiner;

import java.nio.charset.StandardCharsets;

/**
 * @author Benoit, Benedek
 */
public class Request {
  private static final String MESSAGE_DELIMITER = "\n";
  private Command command;
  private String argument;

  public Request(RequestBuilder builder) {
    this.command = builder.command;
    this.argument = builder.argument;
  }

  public Command getCommand() {
    return command;
  }

  public String getArgument() {
    return argument;
  }

  public String toString() {
    return Joiner.on(" ").join(command.toString(), argument, MESSAGE_DELIMITER);
  }

  public byte[] argumentAsBytes() {
    return argument.getBytes(StandardCharsets.US_ASCII);
  }

  public static class RequestBuilder {
    private Command command;
    private String argument;

    public RequestBuilder command(Command command) {
      this.command = command;
      return this;
    }

    public RequestBuilder payload(String argument) {
      this.argument = argument;
      return this;
    }

    public Request build() {
      return new Request(this);
    }
  }
}
