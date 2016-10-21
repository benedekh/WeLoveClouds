package main.java.com.weloveclouds.client.models;

/**
 * Created by Benoit on 2016-10-21.
 */
public class UserInput {
    private String command = "";
    private String payload = "";

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public UserInput withCommand(String command){
        setCommand(command);
        return this;
    }

    public UserInput withPayload(String payload){
        setPayload(payload);
        return this;
    }
}
