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

import java.util.UUID;

public class DatastoreModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(DatastoreModule.class);

    // Env Vars
    private static final String DOCKER_ENV = "STORE_ENV";
    private static final String PRIMARY_ENV = "PRIMARY";
    private static final String FAULT_THRESHOLD_ENV = "FAULT_THRESHOLD";
    private static final String COMMUNITY_ID = "COMMUNITY_ID08022cas8";
    // Akka config files
    private static final String AKKA_DOCKER_CONFIG = "applicationDocker.conf";
    private static final String AKKA_LOCAL_CONFIG = "applicationLocal.conf";

    private static final UUID IDENTITY = UUID.randomUUID();
    private static final int FINAL_COMMUNITY_ID = 0;

    @Override
    protected void configure() {
        log.info("Is docker env: {}", isDockerEnv());
        log.info("Is primary: {}", isPrimary());
        log.info("Fault Threshold: {}", getFaultThreshold());
        log.info("Community ID: {}", getCommunityId());
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
                .identity(IDENTITY.toString())
                .faultThreshold(getFaultThreshold())
                .isPrimary(isPrimary())
                .communityId(getCommunityId())
                .finalCommunityId(getFinalCommunityId())
                .akkaConfig(getAkkaConfig())
                .runLocally(!isDockerEnv())
                .build();
    }

    private Config getAkkaConfig() {
        if (isDockerEnv())  {
            return ConfigFactory.load(AKKA_DOCKER_CONFIG);
        } else {
            return ConfigFactory.load(AKKA_LOCAL_CONFIG);
        }
    }
    
    private boolean isPrimary() {
        String primary = System.getenv(PRIMARY_ENV);
        return primary == null || Boolean.parseBoolean(primary);
    }
    
    private int getFaultThreshold() {
        String faultThreshold = System.getenv(FAULT_THRESHOLD_ENV);
        if (faultThreshold == null) {
            return 0;
        }
        return Integer.parseInt(faultThreshold);
    }

    private int getCommunityId() {
        String communityId = System.getenv(COMMUNITY_ID);
        if (communityId == null) {
            return 0;
        }
        return Integer.parseInt(communityId);
    }

    private int getFinalCommunityId() {
        return FINAL_COMMUNITY_ID;
    }
    
    private boolean isDockerEnv() {
        String env = System.getenv(DOCKER_ENV);
        return env != null && env.equals("docker");
    }
}
