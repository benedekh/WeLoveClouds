package weloveclouds.ecs.core;

/**
 * Created by Benoit on 2016-11-22.
 */
public enum EcsStatus {
    WAITING_FOR_LOAD_BALANCER_INITIALIZATION("Waiting for load balancer initialization"),
    INITIALIZING_SERVICE("Initializing service"), UPDATING_METADATA("Updating metadata"),
    INITIALIZED("Initialized"), STARTING_LOAD_BALANCER("Starting load balancer"),
    STARTING_NODE("Starting node"), STOPPING_NODE("Stopping node"), REMOVING_NODE("Removing node"),
    ADDING_NODE("Adding node"), SHUTTING_DOWN_NODE("Shutting down node"),
    WAITING_FOR_SERVICE_INITIALIZATION("Waiting for service initialization"),
    INITIALIZING_LOAD_BALANCER("Initializing load balancer");
    private String description;

    EcsStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
