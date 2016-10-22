package weloveclouds.communication.models;

/**
 * Created by Benoit on 2016-10-21.
 */
public enum Command {

    SEND("send"),CONNECT("connect"),DISCONNECT("disconnect");

    private String description;

    Command(String description){
        this.description = description;
    }

    @Override
    public String toString(){
        return this.description;
    }
}
