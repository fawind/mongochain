package configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import index.ContentHashIndex;
import index.InMemoryContentHashIndex;
import storage.ContentAddressableStorage;
import storage.IpfsStorage;

public class DatastoreModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ContentHashIndex.class).to(InMemoryContentHashIndex.class);
    }

    @Provides
    @Singleton
    public ContentAddressableStorage getContentAddressableStorage() {
        // TODO: Add ipfs configuration here (host, ...)
        return new IpfsStorage();
    }
}
