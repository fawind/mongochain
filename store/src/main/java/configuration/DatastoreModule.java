package configuration;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import datastore.Datastore;
import datastore.IpfsDatastore;
import index.ContentHashIndex;
import index.InMemoryContentHashIndex;
import io.ipfs.api.IPFS;
import pubsub.IpfsPubSubService;
import pubsub.PubSubService;
import storage.ContentAddressableStorage;
import storage.IpfsStorage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DatastoreModule extends AbstractModule {

    private static final String PUBSUB_TOPIC = "ipfs-store-node";

    @Override
    protected void configure() {
        bind(ContentHashIndex.class).to(InMemoryContentHashIndex.class).in(Singleton.class);
        bind(ContentAddressableStorage.class).to(IpfsStorage.class);
        bind(PubSubService.class).to(IpfsPubSubService.class);
        bindConstant().annotatedWith(PubsubTopic.class).to(PUBSUB_TOPIC);
        bind(Datastore.class).to(IpfsDatastore.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public IPFS getIpfsService() {
        return new IPFSLoader().getIPFS();
    }

    @BindingAnnotation @Retention(RetentionPolicy.RUNTIME)
    public @interface PubsubTopic {}
}
