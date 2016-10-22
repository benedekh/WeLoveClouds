package weloveclouds.communication.models;

import java.nio.charset.StandardCharsets;

/**
 * @author Benoit, Benedek
 */
public class Response {
  private byte[] content;

  public Response(byte[] content) {
    this.content = content;
  }

  public byte[] getContent() {
    return content;
  }

  public String toString() {
    return new String(content, StandardCharsets.US_ASCII);
  }

}

