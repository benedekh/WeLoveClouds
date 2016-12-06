package weloveclouds.loadbalancer.models;

/**
 * Created by Benoit on 2016-12-05.
 */
public class KVHearthbeatMessage {
    private NodeHealthInfos serverHealthInfos;

    public KVHearthbeatMessage(NodeHealthInfos serverHealthInfos) {
        this.serverHealthInfos = serverHealthInfos;
    }

    public NodeHealthInfos getNodeHealthInfos() {
        return serverHealthInfos;
    }
}
