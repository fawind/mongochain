package configuration;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import index.ContentHashIndex;
import index.InMemoryContentHashIndex;
import io.ipfs.api.IPFS;
import storage.ContentAddressableStorage;
import storage.IpfsStorage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DatastoreModule extends AbstractModule {

    // TODO: Move to config file
    private static final String IPFS_ADDRESS = "/ip4/127.0.0.1/tcp/5001";
    private static final String PUBSUB_TOPIC = "ipfs-store-node";

    @Override
    protected void configure() {
        bind(ContentHashIndex.class).to(InMemoryContentHashIndex.class);
        bind(ContentAddressableStorage.class).to(IpfsStorage.class);
        bindConstant().annotatedWith(PubsubTopic.class).to(PUBSUB_TOPIC);
    }

    @Provides
    @Singleton
    public IPFS getIpfsService() {
        return new IPFS(IPFS_ADDRESS);
    }

    @BindingAnnotation @Retention(RetentionPolicy.RUNTIME)
    public @interface PubsubTopic {}
}
