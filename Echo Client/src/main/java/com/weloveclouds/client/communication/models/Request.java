package main.java.com.weloveclouds.client.communication.models;

import com.google.common.base.Joiner;

import java.nio.charset.StandardCharsets;

/**
 * Created by Benoit on 2016-10-21.
 */
public class Request {
    private static final String MESSAGE_DELIMITER = "\n";
    private Command command;
    private String argument;

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public Request withCommand(Command command){
        setCommand(command);
        return this;
    }

    public Request withPayload(String payload){
        setArgument(payload);
        return this;
    }

    public String toString(){
        return Joiner.on(" ").join(command.toString(), argument, MESSAGE_DELIMITER);
    }

    public byte[] toBytes(){
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }
}
