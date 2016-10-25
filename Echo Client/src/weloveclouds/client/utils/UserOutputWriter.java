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
  private static UserOutputWriter instance;
  private BufferedWriter outputWriter;
  private static final String PREFIX = "EchoClient> ";

  private UserOutputWriter() {
    this.outputWriter = new BufferedWriter(new OutputStreamWriter(System.out));
  }

  public void writeLine(String message) throws IOException {
    writePrefix();
    outputWriter.write(message);
    outputWriter.newLine();
    outputWriter.flush();
  }

  public void writePrefix() throws IOException {
    outputWriter.write(PREFIX);
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

  public static UserOutputWriter getInstance(){
    if(UserOutputWriter.instance == null){
      UserOutputWriter.instance = new UserOutputWriter();
    }

    return UserOutputWriter.instance;
  }
}
