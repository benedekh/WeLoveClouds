package weloveclouds.commons.monitoring.models;

/**
 * Denotes a service as an enum.
 * 
 * @author Benoit
 */
public enum Service {
    ECS("ecs"), KV_SERVER("kvserver"), KV_CLIENT("kvclient");

    private String description;

    Service(String description) {
        this.description = description;
    }

    public String toString() {
        return this.description;
    }
}
