package weloveclouds.server.monitoring.heartbeat;

import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.ecs.models.repository.NodeStatus;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Node health monitor, which regularly supervises and reports the health status of the registered
 * services.
 * 
 * @author Benedek
 */
public class NodeHealthMonitor extends Thread {

    private static final Logger LOGGER = Logger.getLogger(NodeHealthMonitor.class);
    private static final Duration WAIT_TIME_BEFORE_HEARTBEAT = new Duration(2 * 1000);

    private NodeHealthInfos.Builder nodeHealthInfosBuilder;
    private List<ServiceHealthMonitor> serviceHealthMonitors;

    private HeartbeatSenderService hearbeatSenderService;

    protected NodeHealthMonitor(Builder builder) {
        this.nodeHealthInfosBuilder = builder.nodeHealthInfosBuilder;
        this.hearbeatSenderService = builder.hearbeatSenderService;
        this.serviceHealthMonitors = builder.serviceHealthMonitors;
    }

    public void setNodeStatus(NodeStatus status) {
        nodeHealthInfosBuilder.nodeStatus(status);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try (HeartbeatSenderService heartbeatSender = this.hearbeatSenderService) {
                hearbeatSenderService.connect();
                NodeHealthInfos nodeHealthInfos = nodeHealthInfosBuilder.build();
                nodeHealthInfos.getServicesHealthInfos().clear();
                for (ServiceHealthMonitor serviceHealthMonitor : serviceHealthMonitors) {
                    nodeHealthInfos.getServicesHealthInfos()
                            .add(serviceHealthMonitor.getHealthInfos());
                }
                heartbeatSender.send(nodeHealthInfos);
                Thread.sleep(WAIT_TIME_BEFORE_HEARTBEAT.getMillis());
            } catch (UnableToSendContentToServerException | UnableToConnectException ex) {
                LOGGER.error(ex);
            } catch (InterruptedException ex) {
                LOGGER.error(ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * A builder to create a {@link NodeHealthMonitor} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private NodeHealthInfos.Builder nodeHealthInfosBuilder;
        private HeartbeatSenderService hearbeatSenderService;
        private List<ServiceHealthMonitor> serviceHealthMonitors;

        public Builder nodeHealthInfosBuilder(NodeHealthInfos.Builder nodeHealthInfosBuilder) {
            this.nodeHealthInfosBuilder = nodeHealthInfosBuilder;
            return this;
        }

        public Builder hearbeatSenderService(HeartbeatSenderService hearbeatSenderService) {
            this.hearbeatSenderService = hearbeatSenderService;
            return this;
        }

        public Builder serviceHealthMonitors(List<ServiceHealthMonitor> serviceHealthMonitors) {
            this.serviceHealthMonitors = serviceHealthMonitors;
            return this;
        }

        public NodeHealthMonitor build() {
            return new NodeHealthMonitor(this);
        }
    }

}
