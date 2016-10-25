package weloveclouds.client.models;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Benedek
 */
public enum Command {
    CONNECT, DISCONNECT, SEND, LOGLEVEL, HELP, QUIT, DEFAULT;

    public static Command fromString(String command) {
        Command enumValue;
        try {
            enumValue = Command.valueOf(command.toUpperCase());
        }catch(IllegalArgumentException ex){
            enumValue = DEFAULT;
        }
        return enumValue;
    }
}
