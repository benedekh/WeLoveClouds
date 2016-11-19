package weloveclouds.ecs.models.commands;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
/**
 * 
 * @author hb, benedekh, benoit
 * An implementantion of a command system like that of the client and of the server
 *
 */
public enum EcsCommand {
    INIT("initService"), START("start"), STOP("stop"), SHUTDOWN("shutdown"),
    ADD_NODE("addnode"), REMOVE_NODE("removenode"), DEFAULT("default"), LOGLEVEL("loglevel"),
    QUIT("quit");
    //do we need a loglevel command?
    
    private static final Logger LOGGER = Logger.getLogger(EcsCommand.class);

    private String customName;
    
    EcsCommand(String customName) {
        this.customName = customName;
    }

    private String getCustomName() {
        return customName;
    }
    
    /**
     * Creates a map from the names of the commands and its command object.
     */
    private static Map<String, EcsCommand> getCommandNames() {
        Map<String, EcsCommand> names = new TreeMap<>();
        for (EcsCommand command : EcsCommand.values()) {
            names.put(command.getCustomName(), command);
        }
        return names;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static EcsCommand convertToString(String command) {
        Map<String, EcsCommand> commandNames = getCommandNames();
        EcsCommand recognized = (command == null || !commandNames.containsKey(command) ? DEFAULT
                : commandNames.get(command));
        if (recognized == DEFAULT) {
            LOGGER.warn(CustomStringJoiner.join("", "Command (", command, ") is not recognized."));
        }
        return recognized;
    }
}
