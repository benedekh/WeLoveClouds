package weloveclouds.loadbalancer.models;

/**
 * Created by Benoit on 2016-12-05.
 */
public class KVHearthbeatMessage {
    private ServerHealthInfos serverHealthInfos;

    public KVHearthbeatMessage(ServerHealthInfos serverHealthInfos) {
        this.serverHealthInfos = serverHealthInfos;
    }
}
