package weloveclouds.ecs.models.tasks;

/**
 * Created by Benoit on 2016-11-22.
 */
public enum BatchPurpose {
    SERVICE_INITIALISATION, START_LOAD_BALANCER, START_NODE, STOP_NODE, SHUTDOWN, ADD_NODE,
    REMOVE_NODE, UPDATING_METADATA
}
