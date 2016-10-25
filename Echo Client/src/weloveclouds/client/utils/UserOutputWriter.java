package weloveclouds.client.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * 
 * @author Benedek
 */
public class UserOutputWriter implements AutoCloseable {

  private BufferedWriter outputWriter;
  private String prefix;

  public UserOutputWriter(String prefix, OutputStream outputStream) {
    this(outputStream);
    this.prefix = prefix;
  }

  public UserOutputWriter(OutputStream outputStream) {
    this.prefix = "EchoClient> ";
    this.outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
  }

  public void writeLine(String message) throws IOException {
    outputWriter.write(message);
    outputWriter.newLine();
    outputWriter.flush();
  }

  public void writePrefix() throws IOException {
    outputWriter.write(prefix);
    outputWriter.flush();
  }

  @Override
  public void close() {
    try {
      outputWriter.close();
    } catch (IOException ex) {
      // suppress exception
    }
  }
}
