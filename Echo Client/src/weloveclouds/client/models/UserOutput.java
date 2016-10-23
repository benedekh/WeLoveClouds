package weloveclouds.client.models;

import com.google.common.base.Joiner;

public class UserOutput {

  private static String prefix;
  private String message;

  public static void setPrefix(String prefix) {
    UserOutput.prefix = prefix;
  }

  public UserOutput(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return Joiner.on(" ").join(prefix, message);
  }

}
