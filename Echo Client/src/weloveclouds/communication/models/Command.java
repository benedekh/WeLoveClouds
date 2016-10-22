package weloveclouds.communication.models;

/**
 * @author Benoit
 */
public enum Command {

  SEND("send"), CONNECT("connect"), DISCONNECT("disconnect");

  private String description;

  Command(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return this.description;
  }
}
