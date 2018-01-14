package webservice.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/ping")
public interface PingService {

    @GET
    Response ping();
}
