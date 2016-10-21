package main.java.com.weloveclouds.client.communication.models;

import com.google.common.base.Joiner;

/**
 * Created by Benoit on 2016-10-21.
 */
public class Request {

    private Command command;
    private String payload;

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Request withCommand(Command command){
        setCommand(command);
        return this;
    }

    public Request withPayload(String payload){
        setPayload(payload);
        return this;
    }

    public String toString(){
        return Joiner.on(" ").join(command.toString(),  payload);
    }
}
