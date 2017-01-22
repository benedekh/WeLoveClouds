package weloveclouds.loadbalancer.rest.api.v1.resources;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import weloveclouds.loadbalancer.rest.api.v1.models.Responses.Pojo;

/**
 * Created by Benoit on 2017-01-22.
 */
@Path("/rest/api/v1/monitoring")
public class MonitoringServiceResource {

    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(new Pojo("test"));
        return Response.ok() //200
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .allow("OPTIONS").build();
    }
}
