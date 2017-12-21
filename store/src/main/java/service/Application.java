package service;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import configuration.DatastoreModule;
import service.resources.PingResource;
import service.resources.StoreResource;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application extends javax.ws.rs.core.Application implements Module {

    private static final Logger log = Logger.getLogger(Application.class.getName());

    @Override
    public void configure(Binder binder) {
        binder.install(new DatastoreModule());
        binder.bind(PingResource.class).in(Singleton.class);
        binder.bind(StoreResource.class).in(Singleton.class);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        log.addHandler(consoleHandler);
    }
}
