package webservice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import configuration.DatastoreModule;
import webservice.resources.PingResource;
import webservice.resources.StoreResource;

public class Application extends javax.ws.rs.core.Application implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new DatastoreModule());
        binder.bind(PingResource.class).in(Singleton.class);
        binder.bind(StoreResource.class).in(Singleton.class);
    }
}
