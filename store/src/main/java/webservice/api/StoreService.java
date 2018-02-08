package webservice.api;

import model.Key;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("store/{namespace}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface StoreService {

    @Path("{key}")
    @GET
    String getValue(@PathParam("namespace") String namespace, @PathParam("key") String key);

    @Path("{key}/{value}")
    @GET
    String setValue(
            @PathParam("namespace") String namespace,
            @PathParam("key") String key,
            @PathParam("value") String value);

    @Path("/index")
    @GET
    Map<Key, String> getIndex();
}
