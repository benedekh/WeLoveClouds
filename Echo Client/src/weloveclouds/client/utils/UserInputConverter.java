package weloveclouds.client.utils;

import weloveclouds.client.models.UserInput;

/**
 * @author Benoit
 */
public interface UserInputConverter<T> {
  T convert(UserInput userInput);
}
