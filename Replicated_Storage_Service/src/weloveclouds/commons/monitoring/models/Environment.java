package weloveclouds.commons.monitoring.models;

/**
 * Created by Benoit on 2016-11-27.
 */
public enum Environment {
    PRODUCTION("prod"), DEVELOPMENT("dev");

    private String description;

    Environment(String description) {
        this.description = description;
    }

    public String toString() {
        return this.description;
    }
}
