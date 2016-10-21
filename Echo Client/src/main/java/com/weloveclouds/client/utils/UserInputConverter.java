package main.java.com.weloveclouds.client.utils;

import main.java.com.weloveclouds.client.communication.models.Request;
import main.java.com.weloveclouds.client.models.UserInput;

/**
 * Created by Benoit on 2016-10-21.
 */
public interface UserInputConverter <T>{
    T convert(UserInput userInput);
}
