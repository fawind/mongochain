package webservice.resources;

import datastore.Datastore;
import index.ContentHashIndex;
import model.DatastoreException;
import model.Key;
import webservice.api.StoreService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Map;

public class StoreResource implements StoreService {

    private final Datastore datastore;
    private final ContentHashIndex index;

    @Inject
    public StoreResource(Datastore datastore, ContentHashIndex index) {
        this.datastore = datastore;
        this.index = index;
    }

    @Override
    public String getValue(String namespace, String key) {
        try {
            return datastore.get(new Key(namespace, key));
        } catch (DatastoreException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Response setValue(String namespace, String key, String value) {
        try {
            datastore.add(new Key(namespace, key), value);
            return Response.ok().build();
        } catch (DatastoreException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @Override
    public Map<Key, String> getIndex() {
        return index.getIndexCopy();
    }
}
