package cluster.actors;

import cluster.messages.NewTransactionMessage;
import cluster.messages.PreprepareMessage;

import java.util.ArrayList;
import java.util.List;

import static cluster.actors.Replica.REPLICA_TOPIC;
import static cluster.logging.Event.logEvent;
import static cluster.logging.EventType.PRIMARY_NEW_PREPREPARE;

public class Primary extends PubSubActor {

    public static final String ACTOR_NAME = "consensus-primary";
    public static final String PRIMARY_TOPIC = "pubsub-primary";

    private final List<PreprepareMessage> messageLog = new ArrayList<>();
    private int sequenceNumber = 0;

    public Primary() {
        observe(PRIMARY_TOPIC);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewTransactionMessage.class, this::handleIncomingTransaction)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handleIncomingTransaction(NewTransactionMessage newTransaction) {
        PreprepareMessage preprepare = new PreprepareMessage(
                sequenceNumber++, newTransaction.getTransaction(), newTransaction.getIdentity());
        log().info(logEvent(PRIMARY_NEW_PREPREPARE, preprepare, getSelf()));
        publish(REPLICA_TOPIC, preprepare);
        messageLog.add(preprepare);
    }
}
