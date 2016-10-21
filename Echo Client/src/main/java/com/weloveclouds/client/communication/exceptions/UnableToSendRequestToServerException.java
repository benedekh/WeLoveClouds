package main.java.com.weloveclouds.client.communication.exceptions;

/**
 * Created by Benoit on 2016-10-21.
 */
public class UnableToSendRequestToServerException extends ClientSideException{
    public UnableToSendRequestToServerException(){
        super("Unable to send request to server.");
    }
}
