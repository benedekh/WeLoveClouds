package weloveclouds.client.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import weloveclouds.client.models.UserOutput;

/**
 * 
 * @author Benedek
 */
public class UserOutputWriter implements AutoCloseable {

  private BufferedWriter outputWriter;

  public UserOutputWriter(OutputStream outputStream) {
    this.outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
  }

  public void write(String message) throws IOException {
    UserOutput output = new UserOutput(message);
    outputWriter.write(output.toString());
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
