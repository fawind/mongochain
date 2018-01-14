package configuration;

import cluster.ConsensusServiceConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import datastore.Datastore;
import datastore.IpfsDatastore;
import datastore.SecuredTransactionLog;
import datastore.TransactionBacklog;
import index.ContentHashIndex;
import index.InMemoryContentHashIndex;
import io.ipfs.api.IPFS;
import storage.ContentAddressableStorage;
import storage.IpfsStorage;

public class DatastoreModule extends AbstractModule {

    private final DatastoreProperties properties = new DatastoreProperties();

    @Override
    protected void configure() {
        bind(Datastore.class).to(IpfsDatastore.class).in(Singleton.class);
        bind(ContentAddressableStorage.class).to(IpfsStorage.class);
        bind(ContentHashIndex.class).to(InMemoryContentHashIndex.class).in(Singleton.class);
        bind(TransactionBacklog.class).in(Singleton.class);
        bind(SecuredTransactionLog.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public IPFS getIpfsService() {
        return new IPFSLoader().getIPFS();
    }

    @Provides
    public ConsensusServiceConfiguration getConsensusServiceConfiguration() {
        return ConsensusServiceConfiguration.builder()
                .port(properties.getAkkaPort())
                .faultThreshold(properties.getFaultThreshold())
                .isPrimary(properties.isPrimary())
                .build();
    }
}
