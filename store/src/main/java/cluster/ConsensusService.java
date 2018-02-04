package cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import cluster.actors.Client;
import cluster.actors.Primary;
import cluster.actors.Replica;
import cluster.messages.NewTransactionMessage;
import datastore.TransactionBacklog;
import io.reactivex.functions.Consumer;
import model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static java.lang.String.format;

public class ConsensusService {

    public static final String SYSTEM_NAME = "consensus-system";
    private static final Logger log = LoggerFactory.getLogger(ConsensusService.class);

    private final ConsensusServiceConfiguration config;
    private final TransactionBacklog transactionBacklog;
    private ActorSystem system;

    @Inject
    public ConsensusService(
            ConsensusServiceConfiguration config,
            TransactionBacklog transactionBacklog) {
        this.config = config;
        this.transactionBacklog = transactionBacklog;
    }

    public void start(Consumer<Transaction> onConsensus) {
        log.info("Starting akka system with config: {}", config);
        if (config.isRunLocally()) {
            startLocally(onConsensus);
            return;
        }
        ActorConfiguration actorConfig = ActorConfiguration.builder()
                .faultThreshold(config.getFaultThreshold())
                .identity(config.getIdentity())
                .communityId(config.getCommunityId())
                .finalCommunityId(config.getFinalCommunityId())
                .onConsensus(onConsensus)
                .build();
        system = ActorSystem.create(SYSTEM_NAME, config.getAkkaConfig());
        if (config.isPrimary()) {
            startPrimary(actorConfig);
        }
        ActorRef client = startClient(actorConfig);
        startReplica(actorConfig);
        subscribeToBacklog(client);
    }

    private ActorRef startReplica(ActorConfiguration actorConfig) {
        return system.actorOf(Props.create(
                Replica.class, () -> new Replica(actorConfig)),
                getActorName(Replica.ACTOR_NAME, actorConfig));
    }

    private ActorRef startClient(ActorConfiguration actorConfig) {
        return system.actorOf(Props.create(
                Client.class, () -> new Client(actorConfig)),
                getActorName(Client.ACTOR_NAME, actorConfig));
    }

    private ActorRef startPrimary(ActorConfiguration actorConfig) {
       return system.actorOf(Props.create(
               Primary.class, () -> new Primary(actorConfig)),
               getActorName(Primary.ACTOR_NAME, actorConfig));
    }

    private void subscribeToBacklog(ActorRef client) {
        transactionBacklog.subscribe(transaction -> {
            NewTransactionMessage newTransactionMessage = new NewTransactionMessage(
                    transaction, config.getIdentity(), false);
            client.tell(newTransactionMessage, ActorRef.noSender());
        });
    }

    private String getActorName(String actorName, ActorConfiguration actorConfig) {
        return format("%s-%d", actorName, actorConfig.getCommunityId());
    }

    /**
     * Dev only for local testing
     */
    private void startLocally(Consumer<Transaction> onConsensus) {
        log.info("Running locally with two communities");
        ActorConfiguration finalConfig = ActorConfiguration.builder()
                .faultThreshold(config.getFaultThreshold())
                .identity(config.getIdentity())
                .communityId(0)
                .finalCommunityId(0)
                .onConsensus(onConsensus)
                .build();
        ActorConfiguration actorConfig = ActorConfiguration.builder()
                .faultThreshold(config.getFaultThreshold())
                .identity(config.getIdentity())
                .communityId(1)
                .finalCommunityId(0)
                .onConsensus(onConsensus)
                .build();
        system = ActorSystem.create(SYSTEM_NAME, config.getAkkaConfig());

        startPrimary(actorConfig);
        ActorRef actorClient = startClient(actorConfig);
        startReplica(actorConfig);
        subscribeToBacklog(actorClient);

        startPrimary(finalConfig);
        startClient(finalConfig);
        startReplica(finalConfig);
    }
}
