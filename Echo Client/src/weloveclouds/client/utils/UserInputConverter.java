package weloveclouds.client.utils;

import weloveclouds.client.models.UserInput;
import weloveclouds.communication.models.Request;

/**
 * Created by Benoit on 2016-10-21.
 */
public interface UserInputConverter <T>{
    T convert(UserInput userInput);
}
