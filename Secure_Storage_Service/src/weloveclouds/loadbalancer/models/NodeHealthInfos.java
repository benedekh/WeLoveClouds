package weloveclouds.loadbalancer.models;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.ecs.models.repository.NodeStatus;

/**
 * Created by Benoit on 2016-12-17.
 */
public class NodeHealthInfos implements Comparable<NodeHealthInfos> {
    private String nodeName;
    private NodeStatus nodeStatus;
    private List<ServiceHealthInfos> servicesHealthInfos;

    protected NodeHealthInfos(Builder builder) {
        this.nodeName = builder.nodeName;
        this.nodeStatus = builder.nodeStatus;
        this.servicesHealthInfos = builder.servicesHealthInfos;
    }

    public String getNodeName() {
        return nodeName;
    }

    public NodeStatus getNodeStatus() {
        return nodeStatus;
    }

    public List<ServiceHealthInfos> getServicesHealthInfos() {
        return servicesHealthInfos;
    }

    private double getHealthIndice() {
        double healthIndice = 0;

        for (ServiceHealthInfos serviceHealthInfos : getServicesHealthInfos()) {
            healthIndice += (serviceHealthInfos.getServicePriority() *
                    serviceHealthInfos.getNumberOfActiveConnections());
        }
        return healthIndice;
    }

    @Override
    public int compareTo(NodeHealthInfos otherServiceHealtInfos) {
        if (getHealthIndice() == otherServiceHealtInfos.getHealthIndice())
            return 0;
        else if (getHealthIndice() > otherServiceHealtInfos.getHealthIndice())
            return 1;
        else
            return -1;
    }
    
    @Override
    public String toString() {
        String servicesHealthInfosStr = StringUtils.join(", ", servicesHealthInfos);
        return StringUtils.join(" ", "{Node name:", nodeName, ", Node status:", nodeStatus,
                ", Service health infos: [", servicesHealthInfosStr, "]}");
    }

    public static class Builder {
        private String nodeName;
        private NodeStatus nodeStatus;
        private List<ServiceHealthInfos> servicesHealthInfos;

        public Builder() {
            servicesHealthInfos = new ArrayList<>();
        }

        public Builder nodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public Builder nodeStatus(NodeStatus nodeStatus) {
            this.nodeStatus = nodeStatus;
            return this;
        }

        public Builder addServiceHealtInfos(ServiceHealthInfos serviceHealthInfos) {
            this.servicesHealthInfos.add(serviceHealthInfos);
            return this;
        }

        public Builder servicesHealtInfos(List<ServiceHealthInfos> servicesHealthInfos) {
            this.servicesHealthInfos = servicesHealthInfos;
            return this;
        }

        public void clearServicesHealthInfos() {
            this.servicesHealthInfos.clear();
        }

        public NodeHealthInfos build() {
            return new NodeHealthInfos(this);
        }
    }
}
