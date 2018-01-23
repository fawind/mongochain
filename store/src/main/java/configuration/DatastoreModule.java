package configuration;

import cluster.ConsensusService;
import cluster.ConsensusServiceConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import datastore.Datastore;
import datastore.IpfsDatastore;
import datastore.SecuredTransactionLog;
import datastore.TransactionBacklog;
import index.ContentHashIndex;
import index.InMemoryContentHashIndex;
import io.ipfs.api.IPFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.ContentAddressableStorage;
import storage.IpfsStorage;

public class DatastoreModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(DatastoreModule.class);
    private static final String DOCKER_ENV = "STORE_ENV";
    private static final String AKKA_DOCKER_CONFIG = "applicationDocker.conf";
    private static final String AKKA_LOCAL_CONFIG = "applicationLocal.conf";

    private final DatastoreProperties properties = new DatastoreProperties();

    @Override
    protected void configure() {
        log.info("Is docker env: {}", isDockerEnv());
        bind(Datastore.class).to(IpfsDatastore.class).asEagerSingleton();
        bind(ContentAddressableStorage.class).to(IpfsStorage.class);
        bind(ConsensusService.class).asEagerSingleton();
        bind(ContentHashIndex.class).to(InMemoryContentHashIndex.class).in(Singleton.class);
        bind(TransactionBacklog.class).in(Singleton.class);
        bind(SecuredTransactionLog.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public IPFS getIpfsService() {
        return new IPFSLoader().getIPFS(isDockerEnv());
    }

    @Provides
    @Singleton
    public ConsensusServiceConfiguration getConsensusServiceConfiguration() {
        return ConsensusServiceConfiguration.builder()
                .port(properties.getAkkaPort())
                .faultThreshold(properties.getFaultThreshold())
                .isPrimary(properties.isPrimary())
                .akkaConfig(getAkkaConfig())
                .build();
    }

    private Config getAkkaConfig() {
        if (isDockerEnv())  {
            return ConfigFactory.load(AKKA_DOCKER_CONFIG);
        } else {
            return ConfigFactory.load(AKKA_LOCAL_CONFIG);
        }
    }

    private boolean isDockerEnv() {
        String env = System.getenv(DOCKER_ENV);
        return env != null && env.equals("docker");
    }
}
