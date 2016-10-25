package weloveclouds.client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import weloveclouds.client.models.UserInput;

/**
 * 
 * @author Benedek
 */
public class UserInputReader implements AutoCloseable {

  private BufferedReader inputReader;

  public UserInputReader(InputStream inputStream) {
    this.inputReader = new BufferedReader(new InputStreamReader(inputStream));
  }

  public UserInput readLine() throws IOException {
    return UserInputParser.parse(inputReader.readLine());
  }

  @Override
  public void close() {
    try {
      inputReader.close();
    } catch (IOException ex) {
      // suppress exception
    }
  }

}
