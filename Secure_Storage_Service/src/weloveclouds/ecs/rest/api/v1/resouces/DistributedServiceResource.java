package weloveclouds.ecs.rest.api.v1.resouces;

import com.google.inject.Singleton;

import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import weloveclouds.ecs.rest.api.v1.models.responses.GetParticipatingNodesResponse;
import weloveclouds.loadbalancer.services.IDistributedSystemAccessService;

/**
 * Created by Benoit on 2017-01-22.
 */
@Singleton
@Path("/rest/api/v1/service")
public class DistributedServiceResource {
    private ObjectMapper objectMapper;
    private IDistributedSystemAccessService distributedSystemAccessService;

    @Inject
    public DistributedServiceResource(IDistributedSystemAccessService
                                                  distributedSystemAccessService) {
        this.distributedSystemAccessService = distributedSystemAccessService;
        this.objectMapper = new ObjectMapper();
    }

    @GET
    @Path("participatingNodes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() throws Exception {
        GetParticipatingNodesResponse response = new GetParticipatingNodesResponse.Builder()
                .participatingNodes(distributedSystemAccessService.getParticipatingNodes())
                .build();
        return Response.ok() //200
                .entity(objectMapper.writeValueAsString(response))
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .allow("OPTIONS")
                .build();
    }
}
