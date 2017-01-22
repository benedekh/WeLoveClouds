package weloveclouds.loadbalancer.rest.api.v1.resources;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Benoit on 2017-01-22.
 */
@Path("/rest/api/v1/loadBalancer")
public class LoadBalancerServiceResource {

    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatus() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return "LoadBalancer";
    }
}
