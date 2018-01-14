package cluster.actors;

import cluster.ActorConfiguration;
import cluster.messages.CommitMessage;
import cluster.messages.PrepareMessage;
import cluster.messages.PreprepareMessage;
import cluster.messages.ResultMessage;

import java.util.ArrayList;
import java.util.List;

import static cluster.actors.Client.CLIENT_TOPIC;

public class Replica extends PubSubActor {

    public static final String ACTOR_NAME = "consensus-replica";
    public static final String REPLICA_TOPIC = "pubsub-replica";

    private final List<PreprepareMessage> preprepareMessageLog = new ArrayList<>();
    private final List<PrepareMessage> prepareMessageLog = new ArrayList<>();
    private final List<CommitMessage> commitMessageLog = new ArrayList<>();
    private final List<ResultMessage> resultLog = new ArrayList<>();
    private final ActorConfiguration config;

    public Replica(ActorConfiguration config) {
        this.config = config;
        pubsubService.observe(REPLICA_TOPIC);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PreprepareMessage.class, this::handlePreprepare)
                .match(PrepareMessage.class, this::handlePrepare)
                .match(CommitMessage.class, this::handleCommit)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handlePreprepare(PreprepareMessage preprepare) {
        PrepareMessage prepare = PrepareMessage.fromPreprepare(preprepare);
        preprepareMessageLog.add(preprepare);
        prepareMessageLog.add(prepare);
        pubsubService.publish(REPLICA_TOPIC, prepare);
    }

    private void handlePrepare(PrepareMessage prepare) {
        prepareMessageLog.add(prepare);
        if (isPrepared(prepare.getSequence())) {
            CommitMessage commit = CommitMessage.fromPreprepare(prepare);
            commitMessageLog.add(commit);
            pubsubService.publish(REPLICA_TOPIC, commit);
        }
    }

    private void handleCommit(CommitMessage commit) {
        commitMessageLog.add(commit);
        if (isCommittedLocally(commit.getSequence())) {
            // TODO: Wait for tasks with lower seq number to finish
            ResultMessage result = ResultMessage.fromCommit(commit);
            if (!resultLog.contains(result)) {
                log().info("Node {} reached consensus on result: {}", getSelf(), result);
                if (!result.getIdentity().equals(config.getIdentity())) {
                    try {
                        config.getOnConsensus().accept(result.getTransaction());
                    } catch (Exception e) {
                        log().error("Error emitting transaction with consensus", e);
                    }
                }
            }
            resultLog.add(result);
            pubsubService.publish(CLIENT_TOPIC, result);
            // TODO: Save message timestamp for new min timestamps for filtering new transactions
        }
    }

    private boolean isPrepared(int sequence) {
        // TODO: Use counter for message log
        return preprepareMessageLog.stream()
                .filter(message -> message.getSequence() == sequence).count() >= config.getFaultThreshold();
    }

    private boolean isCommittedLocally(int sequence) {
        return isPrepared(sequence) &&
                commitMessageLog.stream()
                        .filter(message -> message.getSequence() == sequence).count() >= config.getFaultThreshold();
    }
}
