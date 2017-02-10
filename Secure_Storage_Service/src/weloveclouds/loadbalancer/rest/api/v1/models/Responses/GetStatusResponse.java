package weloveclouds.loadbalancer.rest.api.v1.models.Responses;

import weloveclouds.commons.status.ServerStatus;

/**
 * Created by Benoit on 2017-01-22.
 */
public class GetStatusResponse {
    ServerStatus status;

    public GetStatusResponse(ServerStatus status) {
        this.status = status;
    }

    public String getStatus() {
        return status.name();
    }

    public void setName(ServerStatus status) {
        this.status = status;
    }
}
