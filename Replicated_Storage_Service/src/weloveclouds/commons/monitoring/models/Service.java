package weloveclouds.commons.monitoring.models;

/**
 * Created by Benoit on 2016-11-27.
 */
public enum Service {
    ECS("ecs"), KV_SERVER("kvServer");

    private String description;

    Service(String description) {
        this.description = description;
    }

    public String toString() {
        return this.description;
    }
}
