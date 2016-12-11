package weloveclouds.loadbalancer.models;

/**
 * Created by Benoit on 2016-12-05.
 */
public class KVHeartbeatMessage {
    private NodeHealthInfos serverHealthInfos;

    public KVHeartbeatMessage(NodeHealthInfos serverHealthInfos) {
        this.serverHealthInfos = serverHealthInfos;
    }

    public NodeHealthInfos getNodeHealthInfos() {
        return serverHealthInfos;
    }
}
