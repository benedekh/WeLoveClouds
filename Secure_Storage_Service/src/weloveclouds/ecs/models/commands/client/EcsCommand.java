package weloveclouds.ecs.models.commands.client;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;

/**
 * Different commands which are handled by
 * {@link weloveclouds.ecs.models.commands.client.EcsClientCommandFactory}.
 *
 * @author Benoit, Hunton
 */
public enum EcsCommand {
    START("start"), STOP("stop"), INIT_SERVICE("initService"), SHUTDOWN("shutDown"),
    ADD_NODE("addNode"), REMOVE_NODE("removeNode"), DEFAULT("default"), QUIT("quit"),
    LOGLEVEL("logLevel"), HELP("help"), START_LOAD_BALANCER("startLoadBalancer"), STATS("stats");

    private static final Logger LOGGER = Logger.getLogger(EcsCommand.class);

    private String description;

    EcsCommand(String description) {
        this.description = description;
    }

    /**
     * Converts the parameter to a command if its name matches with one of the commands. Otherwise
     * it returns {@link #DEFAULT}
     */
    public static EcsCommand getValueFromDescription(String description) {
        for (EcsCommand command : EcsCommand.values()) {
            if (command.description.equals(description)) {
                return command;
            }
        }

        LOGGER.warn(StringUtils.join("", "Command (", description, ") is not recognized."));
        return DEFAULT;
    }
}
