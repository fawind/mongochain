package configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import index.ContentHashIndex;
import index.InMemoryContentHashIndex;
import io.ipfs.api.IPFS;
import storage.ContentAddressableStorage;
import storage.IpfsStorage;

public class DatastoreModule extends AbstractModule {

    // TODO: Move to config file
    private static final String IPFS_ADDRESS = "/ip4/127.0.0.1/tcp/5001";

    @Override
    protected void configure() {
        bind(ContentHashIndex.class).to(InMemoryContentHashIndex.class);
        bind(ContentAddressableStorage.class).to(IpfsStorage.class);
    }

    @Provides
    @Singleton
    public IPFS getIpfsService() {
        return new IPFS(IPFS_ADDRESS);
    }
}
