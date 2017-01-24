package weloveclouds.ecs.rest.api.v1.models.responses;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.rest.api.v1.models.pojos.StorageNodePojo;

/**
 * Created by Benoit on 2017-01-23.
 */
public class GetParticipatingNodesResponse {
    List<StorageNodePojo> participatingNodes;

    GetParticipatingNodesResponse(Builder builder) {
        this.participatingNodes = builder.participatingNodes;
    }

    public List<StorageNodePojo> getParticipatingNodes() {
        return participatingNodes;
    }

    public static class Builder {
        private List<StorageNodePojo> participatingNodes;

        public Builder() {
            participatingNodes = new ArrayList<>();
        }

        public Builder participatingNodes(List<StorageNode> participatingNodes) {
            for (StorageNode node : participatingNodes) {
                this.participatingNodes.add(new StorageNodePojo(node));
            }
            return this;
        }

        public GetParticipatingNodesResponse build() {
            return new GetParticipatingNodesResponse(this);
        }
    }
}
