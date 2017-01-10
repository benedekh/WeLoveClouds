package weloveclouds.loadbalancer.models;

/**
 * Created by Benoit on 2016-12-05.
 */
public class KVHeartbeatMessage implements IKVHeartbeatMessage {
    private NodeHealthInfos nodeHealthInfos;

    public KVHeartbeatMessage(NodeHealthInfos nodeHealthInfos) {
        this.nodeHealthInfos = nodeHealthInfos;
    }

    public NodeHealthInfos getNodeHealthInfos() {
        return nodeHealthInfos;
    }
}
