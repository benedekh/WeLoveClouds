package weloveclouds.client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import weloveclouds.client.models.UserInput;

/**
 * The UserInputReader class is used to manage an input stream. 
 * @author Benedek
 */
public class UserInputReader implements AutoCloseable {

  private BufferedReader inputReader;

  /**
   * The constructor for the UserInputReader class
   * @param inputStream - An InputStream object
   */
  public UserInputReader(InputStream inputStream) {
    this.inputReader = new BufferedReader(new InputStreamReader(inputStream));
  }

  /**
   * readLine passes input from the inputReader to the parse function of the UserInputParser class.
   * @return - UserInput object.
   * @throws IOException @see weloveclouds.client.UserInputParser
   */
  public UserInput readLine() throws IOException {
    return UserInputParser.parse(inputReader.readLine());
  }

  /**
   * close closes the inputReader and suppresses any thrown exceptions
   */
  @Override
  public void close() {
    try {
      inputReader.close();
    } catch (IOException ex) {
      // suppress exception
    }
  }

}
