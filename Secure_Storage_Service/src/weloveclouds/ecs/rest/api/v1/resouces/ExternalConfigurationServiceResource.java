package weloveclouds.ecs.rest.api.v1.resouces;

import com.google.inject.Singleton;

import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.rest.api.v1.models.responses.GetRepositoryNodesResponse;
import weloveclouds.ecs.rest.api.v1.models.responses.GetStatusResponse;

/**
 * Created by Benoit on 2017-01-22.
 */
@Singleton
@Path("/rest/api/v1/ecs")
public class ExternalConfigurationServiceResource {
    private ObjectMapper objectMapper;
    private IKVEcsApi ecsApi;

    @Inject
    public ExternalConfigurationServiceResource(IKVEcsApi ecsApi) {
        this.ecsApi = ecsApi;
        this.objectMapper = new ObjectMapper();
    }

    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() throws Exception {
        GetStatusResponse response = new GetStatusResponse(ecsApi.getStatus());
        return Response.ok() //200
                .entity(objectMapper.writeValueAsString(response))
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .allow("OPTIONS")
                .build();
    }

    @GET
    @Path("repository")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepository() throws Exception {
        try {
            GetRepositoryNodesResponse response = new GetRepositoryNodesResponse.Builder()
                    .repositoryNodes(ecsApi.getRepository().getStorageNodes())
                    .build();
            return Response.ok() //200
                    .entity(objectMapper.writeValueAsString(response))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.ok() //200
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        }
    }
}
