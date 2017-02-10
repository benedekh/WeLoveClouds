package weloveclouds.loadbalancer.rest.api.v1.resources;

import com.google.inject.Singleton;

import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import weloveclouds.loadbalancer.rest.api.v1.models.Responses.GetStatusResponse;
import weloveclouds.loadbalancer.services.HealthMonitoringService;

/**
 * Created by Benoit on 2017-01-22.
 */
@Singleton
@Path("/rest/api/v1/monitoring")
public class MonitoringServiceResource {
    private HealthMonitoringService healthMonitoringService;
    private ObjectMapper objectMapper;

    @Inject
    public MonitoringServiceResource(HealthMonitoringService healthMonitoringService) {
        this.healthMonitoringService = healthMonitoringService;
        objectMapper = new ObjectMapper();
    }

    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() throws Exception {
        GetStatusResponse response = new GetStatusResponse(healthMonitoringService.getStatus());
        return Response.ok() //200
                .entity(objectMapper.writeValueAsString(response))
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .allow("OPTIONS")
                .build();
    }
}
