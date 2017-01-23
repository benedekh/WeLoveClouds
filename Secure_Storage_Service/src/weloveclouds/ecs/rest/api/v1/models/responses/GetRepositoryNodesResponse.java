package weloveclouds.ecs.rest.api.v1.models.responses;

import java.util.List;

import weloveclouds.ecs.models.repository.StorageNode;

/**
 * Created by Benoit on 2017-01-23.
 */
public class GetRepositoryNodesResponse {
    List<StorageNode> repositoryNodes;

    GetRepositoryNodesResponse(Builder builder) {
        this.repositoryNodes = builder.repositoryNodes;
    }

    public List<StorageNode> getRepositoryNodes() {
        return repositoryNodes;
    }

    public static class Builder {
        private List<StorageNode> repositoryNodes;

        public Builder repositoryNodes(List<StorageNode> repositoryNodes) {
            this.repositoryNodes = repositoryNodes;
            return this;
        }

        public GetRepositoryNodesResponse build() {
            return new GetRepositoryNodesResponse(this);
        }
    }
}
