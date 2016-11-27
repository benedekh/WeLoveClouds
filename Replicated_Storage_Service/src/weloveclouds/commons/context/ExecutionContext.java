package weloveclouds.commons.context;

import java.util.Arrays;

import static weloveclouds.commons.context.Environment.DEVELOPMENT;
import static weloveclouds.commons.context.Environment.PRODUCTION;

/**
 * Created by Benoit on 2016-11-27.
 */
public class ExecutionContext {
    public static final String EXECUTION_ENVIRONMENT_PROPERTIES = "environment";

    public static Environment getExecutionEnvironmentFromArgs(String[] args) {
        return args != null && Arrays.asList(args).contains(DEVELOPMENT.toString()) ? DEVELOPMENT :
                PRODUCTION;
    }

    public static void setExecutionEnvironmentSystemPropertiesFromArgs(String[] args) {
        System.setProperty(EXECUTION_ENVIRONMENT_PROPERTIES, getExecutionEnvironmentFromArgs
                (args).toString());
    }

    public static Environment getExecutionEnvironment() {
        Environment environment = Environment.getValueFromDescription(System.getProperty
                (EXECUTION_ENVIRONMENT_PROPERTIES));

        if (environment == null) {
            environment = DEVELOPMENT;
        }
        return environment;
    }
}
