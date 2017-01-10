package weloveclouds.server.monitoring.heartbeat;

import org.apache.log4j.Logger;

import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Default heartbeat sender service, which simply logs the health infos (using the {@link #LOGGER})
 * instead of sending them to the recipient.
 * 
 * @author Benedek
 */
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

    /**
     * A builder to create a {@link DefaultHearbeatSenderService} instance.
     *
     * @author Benedek
     */
    public static class Builder extends HeartbeatSenderService.Builder {

        public DefaultHearbeatSenderService build() {
            return new DefaultHearbeatSenderService(this);
        }
    }

}
