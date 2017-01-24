package weloveclouds.ecs.rest.api.v1.models.responses;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.rest.api.v1.models.pojos.StorageNodePojo;

/**
 * Created by Benoit on 2017-01-23.
 */
public class GetRepositoryNodesResponse {
    List<StorageNodePojo> repositoryNodes;

    GetRepositoryNodesResponse(Builder builder) {
        this.repositoryNodes = builder.repositoryNodes;
    }

    public List<StorageNodePojo> getRepositoryNodes() {
        return repositoryNodes;
    }

    public static class Builder {
        private List<StorageNodePojo> repositoryNodes;

        public Builder() {
            repositoryNodes = new ArrayList<>();
        }

        public Builder repositoryNodes(List<StorageNode> repositoryNodes) {
            for (StorageNode node : repositoryNodes) {
                this.repositoryNodes.add(new StorageNodePojo(node));
            }
            return this;
        }

        public GetRepositoryNodesResponse build() {
            return new GetRepositoryNodesResponse(this);
        }
    }
}
