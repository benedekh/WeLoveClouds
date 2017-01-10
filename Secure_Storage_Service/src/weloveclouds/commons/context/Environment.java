package weloveclouds.commons.context;

/**
 * Execution environment enum for the software.
 * 
 * @author Benoit
 */
public enum Environment {
    PRODUCTION("prod"), DEVELOPMENT("dev"), DEBUG("debug");

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
