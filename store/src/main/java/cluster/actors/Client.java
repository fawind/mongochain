package cluster.actors;

import akka.cluster.pubsub.DistributedPubSubMediator.SubscribeAck;
import cluster.ActorConfiguration;
import cluster.messages.GlobalResultMessage;
import cluster.messages.NewTransactionMessage;

import java.util.HashSet;
import java.util.Set;

import static cluster.actors.Primary.PRIMARY_TOPIC;
import static cluster.logging.Event.logEvent;
import static cluster.logging.EventType.CLIENT_INTEGRATE_GLOBAL_RESULT;
import static cluster.logging.EventType.CLIENT_NEW_TRANSACTION;

public class Client extends PubSubActor {

    public static final String ACTOR_NAME = "consensus-client";
    public static final String CLIENT_TOPIC = "pubsub-client";

    private final ActorConfiguration config;
    private final Set<GlobalResultMessage> globalResults = new HashSet<>();

    public Client(ActorConfiguration config) {
        this.config = config;
        observe(CLIENT_TOPIC);
        observe(CLIENT_TOPIC, config.getCommunityId());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewTransactionMessage.class, this::handleNewTransaction)
                .match(GlobalResultMessage.class, this::handleGlobalResult)
                .match(SubscribeAck.class, this::handleSubscribeAck)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handleNewTransaction(NewTransactionMessage newTransaction) {
        log().info(logEvent(CLIENT_NEW_TRANSACTION, newTransaction, getSelf()));
        publish(PRIMARY_TOPIC, config.getCommunityId(), newTransaction);
    }

    private void handleGlobalResult(GlobalResultMessage globalResult) {
        if (globalResults.contains(globalResult)) {
            return;
        }
        log().info(logEvent(CLIENT_INTEGRATE_GLOBAL_RESULT, globalResult, getSelf()));
        globalResults.add(globalResult);
        try {
            config.getOnConsensus().accept(globalResult.getTransaction());
        } catch (Exception e) {
            log().error("Error emitting global consensus message: {}", globalResult, e);
        }
    }
}
