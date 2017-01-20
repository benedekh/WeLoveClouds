package weloveclouds.ecs.models.stats;

import java.util.List;

import weloveclouds.ecs.core.EcsStatus;
import weloveclouds.ecs.models.repository.LoadBalancer;
import weloveclouds.ecs.models.repository.StorageNode;

/**
 * Created by Benoit on 2017-01-18.
 */
public class EcsStatistics {
    private EcsStatus status;
    private LoadBalancer loadBalancer;
    private List<StorageNode> initializedNodes;
    private List<StorageNode> idledNodes;
    private List<StorageNode> runningNodes;
    private List<StorageNode> errorNodes;

    EcsStatistics(Builder builder) {
        this.status = builder.status;
        this.loadBalancer = builder.loadBalancer;
        this.initializedNodes = builder.initializedNodes;
        this.idledNodes = builder.idledNodes;
        this.runningNodes = builder.runningNodes;
        this.errorNodes = builder.errorNodes;
    }

    public EcsStatus getStatus() {
        return status;
    }

    public List<StorageNode> getRunningNodes() {
        return runningNodes;
    }

    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    public List<StorageNode> getInitializedNodes() {
        return initializedNodes;
    }

    public List<StorageNode> getIdledNodes() {
        return idledNodes;
    }

    public List<StorageNode> getErrorNodes() {
        return errorNodes;
    }

    public static class Builder {
        private EcsStatus status;
        private LoadBalancer loadBalancer;
        private List<StorageNode> initializedNodes;
        private List<StorageNode> idledNodes;
        private List<StorageNode> runningNodes;
        private List<StorageNode> errorNodes;

        public Builder status(EcsStatus status) {
            this.status = status;
            return this;
        }

        public Builder loadBalancer(LoadBalancer loadBalancer) {
            this.loadBalancer = loadBalancer;
            return this;
        }

        public Builder initializedNodes(List<StorageNode> initializedNodes) {
            this.initializedNodes = initializedNodes;
            return this;
        }

        public Builder idledNodes(List<StorageNode> idledNodes) {
            this.idledNodes = idledNodes;
            return this;
        }

        public Builder runningNodes(List<StorageNode> runningNodes) {
            this.runningNodes = runningNodes;
            return this;
        }

        public Builder errorNodes(List<StorageNode> errorNodes) {
            this.errorNodes = errorNodes;
            return this;
        }

        public EcsStatistics build() {
            return new EcsStatistics(this);
        }
    }
}
