package cluster.actors;

import akka.cluster.pubsub.DistributedPubSubMediator.SubscribeAck;
import cluster.ActorConfiguration;
import cluster.messages.GlobalResultMessage;
import cluster.messages.LocalResultMessage;
import cluster.messages.NewTransactionMessage;
import cluster.messages.PreprepareMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cluster.actors.Client.CLIENT_TOPIC;
import static cluster.actors.Replica.REPLICA_TOPIC;
import static cluster.logging.Event.logEvent;
import static cluster.logging.EventType.PRIMARY_BROADCAST_GLOBAL_RESULT;
import static cluster.logging.EventType.PRIMARY_BROADCAST_TO_FINAL;
import static cluster.logging.EventType.PRIMARY_NEW_PREPREPARE;
import static cluster.logging.EventType.PRIMARY_RECEIVE_LOCAL_RESULT;

public class Primary extends PubSubActor {

    public static final String ACTOR_NAME = "consensus-primary";
    public static final String PRIMARY_TOPIC = "pubsub-primary";

    private final ActorConfiguration config;
    private final List<PreprepareMessage> messageLog = new ArrayList<>();
    private final Set<LocalResultMessage> localResultMessages = new HashSet<>();
    private int sequenceNumber = 0;

    public Primary(ActorConfiguration config) {
        this.config = config;
        observe(PRIMARY_TOPIC, config.getCommunityId());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewTransactionMessage.class, this::handleIncomingTransaction)
                .match(LocalResultMessage.class, this::handleResultMessage)
                .match(SubscribeAck.class, this::handleSubscribeAck)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handleIncomingTransaction(NewTransactionMessage newTransaction) {
        PreprepareMessage preprepare = PreprepareMessage.fromNewTransaction(sequenceNumber++, newTransaction);
        log().info(logEvent(PRIMARY_NEW_PREPREPARE, preprepare, getSelf()));
        publish(REPLICA_TOPIC, config.getCommunityId(), preprepare);
        messageLog.add(preprepare);
    }

    private void handleResultMessage(LocalResultMessage localResult) {
        if (localResultMessages.contains(localResult)) {
            return;
        }
        log().info(logEvent(PRIMARY_RECEIVE_LOCAL_RESULT, localResult, getSelf()));
        localResultMessages.add(localResult);
        if (!localResult.isFinal()) {
           broadcastToFinalCommunity(localResult);
        } else {
            broadcastGlobalResult(localResult);
        }
    }

    private void broadcastToFinalCommunity(LocalResultMessage localResult) {
        log().info(logEvent(PRIMARY_BROADCAST_TO_FINAL, localResult, getSelf()));
        NewTransactionMessage newTransaction = new NewTransactionMessage(
                localResult.getTransaction(), localResult.getIdentity(), true);
        publish(PRIMARY_TOPIC, config.getFinalCommunityId(), newTransaction);
    }

    private void broadcastGlobalResult(LocalResultMessage localResult) {
        log().info(logEvent(PRIMARY_BROADCAST_GLOBAL_RESULT, localResult, getSelf()));
        GlobalResultMessage globalResult = GlobalResultMessage.fromLocalResult(localResult);
        publish(CLIENT_TOPIC, globalResult);
    }
}
