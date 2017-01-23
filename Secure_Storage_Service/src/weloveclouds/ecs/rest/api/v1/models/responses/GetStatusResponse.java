package weloveclouds.ecs.rest.api.v1.models.responses;

import weloveclouds.ecs.core.EcsStatus;

/**
 * Created by Benoit on 2017-01-23.
 */
public class GetStatusResponse {
    EcsStatus status;

    public GetStatusResponse(EcsStatus status) {
        this.status = status;
    }

    public String getStatus() {
        return status.getDescription();
    }

    public void setStatus(EcsStatus status) {
        this.status = status;
    }
}
