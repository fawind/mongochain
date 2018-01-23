package cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import cluster.actors.Client;
import cluster.actors.Primary;
import cluster.actors.Replica;
import cluster.messages.NewTransactionMessage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import datastore.IdentityProvider;
import datastore.TransactionBacklog;
import io.reactivex.functions.Consumer;
import model.Transaction;

import javax.inject.Inject;

public class ConsensusService {

    public static final String SYSTEM_NAME = "consensus-system";

    private final ConsensusServiceConfiguration config;
    private final TransactionBacklog transactionBacklog;
    private final IdentityProvider identityProvider;
    private ActorSystem system;
    private ActorRef primary;
    private ActorRef replica;
    private ActorRef client;

    @Inject
    public ConsensusService(
            ConsensusServiceConfiguration config,
            TransactionBacklog transactionBacklog,
            IdentityProvider identityProvider) {
        this.config = config;
        this.transactionBacklog = transactionBacklog;
        this.identityProvider = identityProvider;
    }

    public void start(Consumer<Transaction> onConsensus) {
        ActorConfiguration actorConfig = ActorConfiguration.builder()
                .faultThreshold(config.getFaultThreshold())
                .identity(identityProvider.get())
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
                    transaction, identityProvider.get().toString());
            client.tell(newTransactionMessage, ActorRef.noSender());
        });
    }
}
