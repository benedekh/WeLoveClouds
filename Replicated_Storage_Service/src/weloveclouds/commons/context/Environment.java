package weloveclouds.commons.context;

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

    public static Environment getValueFromDescription(String description) {
        Environment environment = null;

        for (Environment env : Environment.values()) {
            if (env.description == description) {
                environment = env;
            }
        }
        return environment;
    }
}
