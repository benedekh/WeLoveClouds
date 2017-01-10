package weloveclouds.commons.context;

import static weloveclouds.commons.context.Environment.DEBUG;
import static weloveclouds.commons.context.Environment.DEVELOPMENT;
import static weloveclouds.commons.context.Environment.PRODUCTION;

import java.util.Arrays;

/**
 * Execution context of the software.
 * 
 * @author Benoit
 */
public class ExecutionContext {
    public static final String EXECUTION_ENVIRONMENT_PROPERTIES = "environment";

    /**
     * Decides if the execution environment is {@value Environment#DEVELOPMENT} or
     * {@link Environment#PRODUCTION}. If the arguments contain the previous, then it is a
     * development environment, otherwise the latter.
     */
    public static Environment getExecutionEnvironmentFromArgs(String[] args) {
        return args != null && Arrays.asList(args).contains(DEVELOPMENT.toString()) ? DEVELOPMENT
                : PRODUCTION;
    }

    /**
     * Sets the environmental variable called {@link #EXECUTION_ENVIRONMENT_PROPERTIES} based on the
     * arguments. If the arguments contain {@value Environment#DEVELOPMENT}, then it is a
     * development environment, otherwise it is a {@link Environment#PRODUCTION}.
     */
    public static void setExecutionEnvironmentSystemPropertiesFromArgs(String[] args) {
        System.setProperty(EXECUTION_ENVIRONMENT_PROPERTIES,
                getExecutionEnvironmentFromArgs(args).toString());
    }

    /**
     * Gets the execution environment.
     */
    public static Environment getExecutionEnvironment() {
        Environment environment = Environment
                .getValueFromDescription(System.getProperty(EXECUTION_ENVIRONMENT_PROPERTIES));

        if (environment == null) {
            environment = DEBUG;
        }
        return environment;
    }
}
