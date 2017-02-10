package weloveclouds.server.client.commands;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;

/**
 * Different commands which are handled by {@link ServerCommandFactory}.
 *
 * @author Benedek, Hunton
 */
public enum ServerCommand {
    CACHESIZE("cacheSize"), HELP("help"), LOGLEVEL("logLevel"), CLIENT_PORT(
            "clientPort"), SERVER_PORT("serverPort"), ECS_PORT("ecsPort"), START(
                    "start"), STORAGEPATH(
                            "storagePath"), STRATEGY("strategy"), QUIT("quit"), DEFAULT("default");

    private static final Logger LOGGER = Logger.getLogger(ServerCommand.class);

    private String description;

    ServerCommand(String description) {
        this.description = description;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static ServerCommand getValueFromDescription(String description) {
        for (ServerCommand command : ServerCommand.values()) {
            if (command.description.equals(description)) {
                return command;
            }
        }

        LOGGER.warn(StringUtils.join("", "Command (", description, ") is not recognized."));
        return DEFAULT;
    }

}

