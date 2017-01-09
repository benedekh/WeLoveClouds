package weloveclouds.server.monitoring.heartbeat;

import org.apache.log4j.Logger;

import weloveclouds.loadbalancer.models.NodeHealthInfos;

public class DefaultHearbeatSenderService extends HeartbeatSenderService {

    private static final Logger LOGGER = Logger.getLogger(DefaultHearbeatSenderService.class);

    protected DefaultHearbeatSenderService(Builder builder) {}

    @Override
    public void send(NodeHealthInfos healthInfos) {
        LOGGER.debug(healthInfos);
    }

    @Override
    public void connect() {}

    @Override
    public void close() {}


    public static class Builder extends HeartbeatSenderService.Builder {

        public DefaultHearbeatSenderService build() {
            return new DefaultHearbeatSenderService(this);
        }
    }

}
