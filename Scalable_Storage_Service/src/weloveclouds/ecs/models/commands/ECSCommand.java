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
public enum ECSCommand {
    INIT("initService"), START("start"), STOP("stop"), SHUTDOWN("shutdown"),
    ADDNODE("addnode"), REMOVENODE("removenode"), DEFAULT("default"), LOGLEVEL("loglevel");
    //do we need a loglevel command?
    
    private static final Logger LOGGER = Logger.getLogger(ECSCommand.class);

    private static final Map<String, ECSCommand> commandNames = getCommandNames();

    private String customName;
    
    ECSCommand(String customName) {
        this.customName = customName;
    }

    private String getCustomName() {
        return customName;
    }
    
    /**
     * Creates a map from the names of the commands and its command object.
     */
    private static Map<String, ECSCommand> getCommandNames() {
        Map<String, ECSCommand> names = new TreeMap<>();
        for (ECSCommand command : ECSCommand.values()) {
            names.put(command.getCustomName(), command);
        }
        return names;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static ECSCommand fromString(String command) {
        ECSCommand recognized = (command == null || !commandNames.containsKey(command) ? DEFAULT
                : commandNames.get(command));
        if (recognized == DEFAULT) {
            LOGGER.warn(CustomStringJoiner.join("", "Command (", command, ") is not recognized."));
        }
        return recognized;
    }
}
