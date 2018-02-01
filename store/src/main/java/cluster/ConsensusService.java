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

public class ConsensusService {

    public static final String SYSTEM_NAME = "consensus-system";
    private static final Logger log = LoggerFactory.getLogger(ConsensusService.class);

    private final ConsensusServiceConfiguration config;
    private final TransactionBacklog transactionBacklog;
    private ActorSystem system;
    private ActorRef primary;
    private ActorRef replica;
    private ActorRef client;

    @Inject
    public ConsensusService(
            ConsensusServiceConfiguration config,
            TransactionBacklog transactionBacklog) {
        this.config = config;
        this.transactionBacklog = transactionBacklog;
    }

    public void start(Consumer<Transaction> onConsensus) {
        log.info("Starting akka system with config: {}", config);
        ActorConfiguration actorConfig = ActorConfiguration.builder()
                .faultThreshold(config.getFaultThreshold())
                .identity(config.getIdentity())
                .onConsensus(onConsensus)
                .build();
        system = ActorSystem.create(SYSTEM_NAME, config.getAkkaConfig());
        if (config.isPrimary()) {
            startPrimary();
        }
        startClient(actorConfig);
        startReplica(actorConfig);
        subscribeToBacklog();
    }

    private void startReplica(ActorConfiguration actorConfig) {
        replica = system.actorOf(Props.create(
                Replica.class, () -> new Replica(actorConfig)),
                Replica.ACTOR_NAME);
    }

    private void startClient(ActorConfiguration actorConfig) {
        client = system.actorOf(Props.create(
                Client.class, () -> new Client(actorConfig)),
                Client.ACTOR_NAME);
    }

    private void startPrimary() {
       primary = system.actorOf(Props.create(Primary.class), Primary.ACTOR_NAME);
    }

    private void subscribeToBacklog() {
        transactionBacklog.subscribe(transaction -> {
            NewTransactionMessage newTransactionMessage = new NewTransactionMessage(
                    transaction, config.getIdentity());
            client.tell(newTransactionMessage, ActorRef.noSender());
        });
    }
}
