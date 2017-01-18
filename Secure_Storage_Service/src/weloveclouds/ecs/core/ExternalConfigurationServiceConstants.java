package weloveclouds.ecs.core;

/**
 * Created by Benoit on 2016-11-20.
 */
public class ExternalConfigurationServiceConstants {
    public static int MAX_NUMBER_OF_LOADBALANCER_INITIALISATION_RETRIES = 3;
    public static int MAX_NUMBER_OF_NODE_INITIALISATION_RETRIES = 3;
    public static int MAX_NUMBER_OF_NODE_START_RETRIES = 3;
    public static int MAX_NUMBER_OF_NODE_STOP_RETRIES = 3;
    public static int MAX_NUMBER_OF_NODE_SHUTDOWN_RETRIES = 3;
    public static final int ECS_REQUESTS_PORT = 30000;
    public static String KV_SERVER_JAR_PATH = "/tmp/ms5-server.jar";
    public static String LB_SERVER_JAR_PATH = "/tmp/ms5-loadbalancer.jar";
}
