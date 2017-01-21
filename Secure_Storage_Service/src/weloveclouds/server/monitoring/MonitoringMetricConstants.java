package weloveclouds.server.monitoring;

/**
 * Constants for metric monitoring.
 * 
 * @author Benedek
 */
public class MonitoringMetricConstants {
    public static final String KVSTORE_MODULE_NAME = "kvstore";
    public static final String CACHE_MODULE_NAME = "cache";

    public static final String PUT_COMMAND_NAME = "registerPut";
    public static final String GET_COMMAND_NAME = "registerGet";
    public static final String REMOVE_COMMAND_NAME = "registerRemove";

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String NOT_RESPONSIBLE = "not_responsible";

    public static final String MISS = "miss";

    public static final String EXEC_TIME = "exec_time";
}
