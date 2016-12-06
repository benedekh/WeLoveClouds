package weloveclouds.commons.context;

/**
 * Created by Benoit on 2016-11-27.
 */
public enum Environment {
    PRODUCTION("prod"), DEVELOPMENT("dev"), DEFAULT("default");

    private String description;

    Environment(String description) {
        this.description = description;
    }

    public String toString() {
        return this.description;
    }

    public static Environment getValueFromDescription(String description) {
        for (Environment environment : Environment.values()) {
            if (environment.description.equals(description)) {
                return environment;
            }
        }
        return null;
    }
}
