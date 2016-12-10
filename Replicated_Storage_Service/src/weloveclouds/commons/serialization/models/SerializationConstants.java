package weloveclouds.commons.serialization.models;

import java.util.regex.Pattern;

/**
 * Created by Benoit on 2016-12-08.
 */
public class SerializationConstants {
    public static final String ACTIVE_CONNECTIONS_START_TOKEN = "<ACTIVE_CONNECTIONS>";
    public static final String ACTIVE_CONNECTIONS_END_TOKEN = "</ACTIVE_CONNECTIONS>";
    public static final String KVHEARTHBEAT_MESSAGE_START_TOKEN = "<KVHEARTHBEAT_MESSAGE>";
    public static final String KVHEARTHBEAT_MESSAGE_END_TOKEN = "</KVHEARTHBEAT_MESSAGE>";
    public static final String TOPOLOGY_START_TOKEN = "<TOPOLOGY>";
    public static final String TOPOLOGY_END_TOKEN = "</TOPOLOGY>";
    public static final String ORDERED_NODES_START_TOKEN = "<ORDERED_NODES>";
    public static final String ORDERED_NODES_END_TOKEN = "</ORDERED_NODES>";
    public static final String NODE_START_TOKEN = "<NODE>";
    public static final String NODE_END_TOKEN = "</NODE>";
    public static final String SERVER_CONNECTION_START_TOKEN = "<CONNECTION_INFOS>";
    public static final String SERVER_CONNECTION_END_TOKEN = "</CONNECTION_INFOS>";
    public static final String NAME_START_TOKEN = "<NAME>";
    public static final String NAME_END_TOKEN = "</NAME>";
    public static final String HASH_KEY_START_TOKEN = "<HASH_KEY>";
    public static final String HASH_KEY_END_TOKEN = "</HASH_KEY>";
    public static final String HASH_RANGE_START_TOKEN = "<HASH_RANGE>";
    public static final String HASH_RANGE_END_TOKEN = "</HASH_RANGE>";
    public static final String CHILD_HASH_RANGES_START_TOKEN = "<CHILD_HASH_RANGES>";
    public static final String CHILD_HASH_RANGES_END_TOKEN = "</CHILD_HASH_RANGES>";
    public static final String CHILD_HASH_RANGE_START_TOKEN = "<CHILD_HASH_RANGE>";
    public static final String CHILD_HASH_RANGE_END_TOKEN = "</CHILD_HASH_RANGE>";
    public static final String NODE_HEALTH_INFOS_START_TOKEN = "<HEALTH_INFOS>";
    public static final String NODE_HEALTH_INFOS_END_TOKEN = "</HEALTH_INFOS>";
    public static final String REPLICAS_START_TOKEN = "<REPLICAS>";
    public static final String REPLICAS_END_TOKEN = "</REPLICAS>";
    public static final String REPLICA_START_TOKEN = "<REPLICA>";
    public static final String REPLICA_END_TOKEN = "</REPLICA>";


    public static final String NODE_HEALTH_INFOS_GROUP = "nodeHealthInfos";
    public static final String SERVER_CONNECTION_GROUP = "serverConnection";
    public static final String NAME_GROUP = "name";
    public static final String ACTIVE_CONNECTIONS_GROUP = "activeConnections";
    public static final String ORDERED_NODES_GROUP = "orderedNodes";
    public static final String NODE_GROUP = "node";
    public static final String HASH_RANGE_GROUP = "hashRange";
    public static final String CHILD_HASH_RANGES_GROUP = "childHashRanges";
    public static final String CHILD_HASH_RANGE_GROUP = "childHashRange";
    public static final String REPLICAS_GROUP = "replicas";
    public static final String REPLICA_GROUP = "replica";


    public static final Pattern NODE_HEALTH_INFOS_REGEX = Pattern.compile
            (NODE_HEALTH_INFOS_START_TOKEN + "(?<nodeHealthInfos>.+?)" + NODE_HEALTH_INFOS_END_TOKEN);
    public static final Pattern SERVER_CONNECTION_INFOS_REGEX = Pattern.compile
            (SERVER_CONNECTION_START_TOKEN + "(?<serverConnection>.+?)" + SERVER_CONNECTION_END_TOKEN);
    public static final Pattern NAME_REGEX = Pattern.compile
            (NAME_START_TOKEN + "(?<name>.+?)" + NAME_END_TOKEN);
    public static final Pattern ACTIVE_NUMBER_OF_CONNECTIONS_REGEX = Pattern.compile
            (ACTIVE_CONNECTIONS_START_TOKEN + "(?<activeConnections>.+?)" + ACTIVE_CONNECTIONS_END_TOKEN);
    public static final Pattern ORDERED_NODES_REGEX = Pattern.compile
            (ORDERED_NODES_START_TOKEN + "(?<orderedNodes>.+)" + ORDERED_NODES_END_TOKEN);
    public static final Pattern NODE_REGEX = Pattern.compile
            (NODE_START_TOKEN + "(?<node>.+?)" + NODE_END_TOKEN);
    public static final Pattern HASH_RANGE_REGEX = Pattern.compile
            (HASH_RANGE_START_TOKEN + "(?<hashRange>.+?)" + HASH_RANGE_END_TOKEN);
    public static final Pattern CHILD_HASH_RANGES_REGEX = Pattern.compile
            (CHILD_HASH_RANGES_START_TOKEN + "(?<childHashRanges>.+?)" + CHILD_HASH_RANGES_END_TOKEN);
    public static final Pattern CHILD_HASH_RANGE_REGEX = Pattern.compile
            (CHILD_HASH_RANGE_START_TOKEN + "(?<childHashRange>.+?)" + CHILD_HASH_RANGE_END_TOKEN);
    public static final Pattern REPLICAS_REGEX = Pattern.compile
            (REPLICAS_START_TOKEN + "(?<replicas>.+?)" + REPLICAS_END_TOKEN);
    public static final Pattern REPLICA_REGEX = Pattern.compile
            (REPLICA_START_TOKEN + "(?<replica>.+?)" + REPLICA_END_TOKEN);

}
