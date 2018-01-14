package cluster.actors;

import cluster.ActorConfiguration;
import cluster.messages.NewTransactionMessage;
import cluster.messages.ResultMessage;

import java.util.ArrayList;
import java.util.List;

import static cluster.actors.Primary.PRIMARY_TOPIC;

public class Client extends PubSubActor {

    public static final String ACTOR_NAME = "consensus-client";
    public static final String CLIENT_TOPIC = "pubsub-client";

    private final List<ResultMessage> resultMessageLog = new ArrayList<>();
    private final List<ResultMessage> resultLog = new ArrayList<>();
    private final ActorConfiguration config;

    public Client(ActorConfiguration config) {
        this.config = config;
        pubsubService.observe(CLIENT_TOPIC);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewTransactionMessage.class, this::handleNewTransaction)
                .match(ResultMessage.class, this::handleResult)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handleNewTransaction(NewTransactionMessage newTransaction) {
        pubsubService.publish(PRIMARY_TOPIC, newTransaction);
    }

    private void handleResult(ResultMessage result) {
        if (!result.getIdentity().equals(config.getIdentity())) {
            return;
        }
        resultMessageLog.add(result);
        if (reachedConsensus(result.getSequence())) {
            if (!resultLog.contains(result)) {
                log().info("Node {} reached consensus on result: {}", getSelf(), result);
                try {
                    config.getOnConsensus().accept(result.getTransaction());
                } catch (Exception e) {
                    log().error("Error emitting transaction with consensus", e);
                }
            }
            resultLog.add(result);
        }
    }

    private boolean reachedConsensus(int sequence) {
        return resultMessageLog.stream()
                .filter(message -> message.getSequence() == sequence).count() >= config.getFaultThreshold();
    }
}
