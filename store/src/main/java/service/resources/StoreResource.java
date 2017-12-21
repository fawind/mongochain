package service.resources;

import datastore.Datastore;
import service.api.StoreService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class StoreResource implements StoreService {

    private final Datastore datastore;

    @Inject
    public StoreResource(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public String getValue(String namespace, String key) {
        return datastore.get(namespace, key);
    }

    @Override
    public Response setValue(String namespace, String key, String value) {
        datastore.add(namespace, key, value);
        return Response.ok().build();
    }
}
