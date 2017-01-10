package weloveclouds.server.configuration.models;

import weloveclouds.ecs.core.ExternalConfigurationServiceConstants;

/**
 * Constants for ports on which KVServer accepts different requests.
 * 
 * @author Benedek
 */
public class KVServerPortConstants {
    public static final int KVCLIENT_REQUESTS_PORT = 50000;
    public static final int KVSERVER_REQUESTS_PORT = 50001;
    public static final int KVECS_REQUESTS_PORT =
            ExternalConfigurationServiceConstants.ECS_REQUESTS_PORT;
}
