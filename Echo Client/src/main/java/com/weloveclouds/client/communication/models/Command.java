package main.java.com.weloveclouds.client.communication.models;

/**
 * Created by Benoit on 2016-10-21.
 */
public enum Command {

    SEND("send");

    private String description;

    Command(String description){
        this.description = description;
    }

    @Override
    public String toString(){
        return this.description;
    }
}
