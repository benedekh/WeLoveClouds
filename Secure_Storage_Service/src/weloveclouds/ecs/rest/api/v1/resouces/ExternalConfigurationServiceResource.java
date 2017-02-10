package weloveclouds.ecs.rest.api.v1.resouces;

import com.google.inject.Singleton;

import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import weloveclouds.commons.jetty.models.ErrorResponse;
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
        return Response.ok()
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
            return Response.ok()
                    .entity(objectMapper.writeValueAsString(response))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        } catch (Exception e) {
            ErrorResponse response = new ErrorResponse().withMessage(e.getMessage());
            return Response.serverError()
                    .entity(objectMapper.writeValueAsString(response))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @POST
    @Path("startLoadBalancer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startLoadBalancer() throws Exception {
        try {
            ecsApi.startLoadBalancer();
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } catch (Exception e) {
            ErrorResponse response = new ErrorResponse().withMessage(e.getMessage());
            return Response.serverError()
                    .entity(objectMapper.writeValueAsString(response))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @POST
    @Path("initService")
    @Produces(MediaType.APPLICATION_JSON)
    public Response initService(@QueryParam("numberOfNodes") int numberOfNodes, @QueryParam
            ("cacheSize") int cacheSize, @QueryParam("displacementStrategy") String
                                        displacementStrategy) throws Exception {
        try {
            ecsApi.initService(numberOfNodes, cacheSize, displacementStrategy);
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } catch (Exception e) {
            ErrorResponse response = new ErrorResponse().withMessage(e.getMessage());
            return Response.serverError()
                    .entity(objectMapper.writeValueAsString(response))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @POST
    @Path("startNode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startNode() throws Exception {
        try {
            ecsApi.start();
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } catch (Exception e) {
            ErrorResponse response = new ErrorResponse().withMessage(e.getMessage());
            return Response.serverError()
                    .entity(objectMapper.writeValueAsString(response))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @POST
    @Path("stopNode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopNode() throws Exception {
        try {
            ecsApi.stop();
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } catch (Exception e) {
            ErrorResponse response = new ErrorResponse().withMessage(e.getMessage());
            return Response.serverError()
                    .entity(objectMapper.writeValueAsString(response))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @POST
    @Path("addNode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNode(@QueryParam("cacheSize") int cacheSize,
                                @QueryParam("displacementStrategy") String displacementStrategy,
                                @QueryParam("autoStart") String autoStart) throws Exception {
        try {
            if(autoStart.equals("True")) {
                ecsApi.addNode(cacheSize, displacementStrategy, true);
            }else{
                ecsApi.addNode(cacheSize, displacementStrategy, false);
            }

            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } catch (Exception e) {
            ErrorResponse response = new ErrorResponse().withMessage(e.getMessage());
            return Response.serverError()
                    .entity(objectMapper.writeValueAsString(response))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @POST
    @Path("removeNode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeNode() throws Exception {
        try {
            ecsApi.removeNode();
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } catch (Exception e) {
            ErrorResponse response = new ErrorResponse().withMessage(e.getMessage());
            return Response.serverError()
                    .entity(objectMapper.writeValueAsString(response))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @POST
    @Path("shutdown")
    @Produces(MediaType.APPLICATION_JSON)
    public Response shutdown() throws Exception {
        try {
            ecsApi.shutDown();
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } catch (Exception e) {
            ErrorResponse response = new ErrorResponse().withMessage(e.getMessage());
            return Response.serverError()
                    .entity(objectMapper.writeValueAsString(response))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }
}
