package weloveclouds.loadbalancer.models;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.commons.status.ServerStatus;

/**
 * Created by Benoit on 2016-12-17.
 */
public class NodeHealthInfos {
    private String nodeName;
    private ServerStatus nodeStatus;
    private List<ServiceHealthInfos> servicesHealthInfos;

    protected NodeHealthInfos(Builder builder) {
        this.nodeName = builder.nodeName;
        this.nodeStatus = builder.nodeStatus;
        this.servicesHealthInfos = builder.servicesHealthInfos;
    }

    public static class Builder {
        private String nodeName;
        private ServerStatus nodeStatus;
        private List<ServiceHealthInfos> servicesHealthInfos;

        public Builder() {
            servicesHealthInfos = new ArrayList<>();
        }

        public Builder nodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public Builder nodeStatus(ServerStatus serverStatus) {
            this.nodeStatus = nodeStatus;
            return this;
        }

        public Builder addServiceHealtInfos(ServiceHealthInfos serviceHealthInfos) {
            this.servicesHealthInfos.add(serviceHealthInfos);
            return this;
        }

        public NodeHealthInfos build() {
            return new NodeHealthInfos(this);
        }
    }
}
