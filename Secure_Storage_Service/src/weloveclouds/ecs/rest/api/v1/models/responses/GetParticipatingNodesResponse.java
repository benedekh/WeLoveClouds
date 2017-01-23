package weloveclouds.ecs.rest.api.v1.models.responses;

import java.util.List;

import weloveclouds.ecs.models.repository.StorageNode;

/**
 * Created by Benoit on 2017-01-23.
 */
public class GetParticipatingNodesResponse {
    List<StorageNode> participatingNodes;

    GetParticipatingNodesResponse(Builder builder) {
        this.participatingNodes = builder.participatingNodes;
    }

    public List<StorageNode> getParticipatingNodes() {
        return participatingNodes;
    }

    public static class Builder {
        private List<StorageNode> participatingNodes;

        public Builder participatingNodes(List<StorageNode> participatingNodes) {
            this.participatingNodes = participatingNodes;
            return this;
        }

        public GetParticipatingNodesResponse build() {
            return new GetParticipatingNodesResponse(this);
        }
    }
}
