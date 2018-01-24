package cluster.actors;

import cluster.ActorConfiguration;
import cluster.messages.CommitMessage;
import cluster.messages.PrepareMessage;
import cluster.messages.PreprepareMessage;
import cluster.messages.ResultMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static cluster.actors.Client.CLIENT_TOPIC;
import static cluster.logging.Event.logEvent;
import static cluster.logging.EventType.REPLICA_CONSENSUS_RESULT;
import static cluster.logging.EventType.REPLICA_INTEGRATE_RESULT;
import static cluster.logging.EventType.REPLICA_NEW_COMMIT;
import static cluster.logging.EventType.REPLICA_NEW_PREPARE;
import static cluster.logging.EventType.REPLICA_NEW_RESULT;
import static cluster.logging.EventType.REPLICA_RECEIVE_COMMIT;
import static cluster.logging.EventType.REPLICA_RECEIVE_PREPARE;
import static com.google.common.collect.Iterables.getLast;

public class Replica extends PubSubActor {

    public static final String ACTOR_NAME = "consensus-replica";
    public static final String REPLICA_TOPIC = "pubsub-replica";

    private final List<PreprepareMessage> preprepareMessageLog = new ArrayList<>();
    private final List<PrepareMessage> prepareMessageLog = new ArrayList<>();
    private final List<CommitMessage> commitMessageLog = new ArrayList<>();
    private final List<ResultMessage> resultLog = new ArrayList<>();
    private final PriorityQueue<ResultMessage> pendingResults =
            new PriorityQueue<>(10, Comparator.comparingInt(ResultMessage::getSequence));
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
        log().info(logEvent(REPLICA_NEW_PREPARE, prepare, getSelf()));
        pubsubService.publish(REPLICA_TOPIC, prepare);
    }

    private void handlePrepare(PrepareMessage prepare) {
        prepareMessageLog.add(prepare);
        log().info(logEvent(REPLICA_RECEIVE_PREPARE, prepare, getSelf()));
        if (isPrepared(prepare.getSequence())) {
            CommitMessage commit = CommitMessage.fromPreprepare(prepare);
            commitMessageLog.add(commit);
            log().info(logEvent(REPLICA_NEW_COMMIT, commit,getSelf()));
            pubsubService.publish(REPLICA_TOPIC, commit);
        }
    }

    private void handleCommit(CommitMessage commit) {
        log().info(logEvent(REPLICA_RECEIVE_COMMIT, commit, getSelf()));
        commitMessageLog.add(commit);
        if (isCommittedLocally(commit.getSequence())) {
            ResultMessage result = ResultMessage.fromCommit(commit);
            log().info(logEvent(REPLICA_NEW_RESULT, result, getSelf()));
            // TODO: Handle lost messages
            if (isNextResult(result)) {
                processValidatedResult(result);
                processPendingResults();
            } else {
                pendingResults.add(result);
            }
        }
    }
    
    private void processValidatedResult(ResultMessage result) {
        log().info(logEvent(REPLICA_CONSENSUS_RESULT, result, getSelf()));
        if (!resultLog.contains(result)) {
            if (!result.getIdentity().equals(config.getIdentity())) {
                log().info(logEvent(REPLICA_INTEGRATE_RESULT, result, getSelf()));
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
    
    private void processPendingResults() {
        while (!pendingResults.isEmpty() && isNextResult(pendingResults.peek())) {
            ResultMessage result = pendingResults.poll();
            processValidatedResult(result);
        }
    }
    
    private boolean isPrepared(int sequence) {
        // TODO: Use counter for message log
        return preprepareMessageLog.stream()
                .filter(message -> message.getSequence() == sequence).count() >= config.getFaultThreshold();
    }
    
    private boolean isNextResult(ResultMessage result) {
        if (resultLog.isEmpty()) {
            return result.getSequence() == 0;
        }
        return getLast(resultLog).getSequence() + 1 == result.getSequence();
    }

    private boolean isCommittedLocally(int sequence) {
        return isPrepared(sequence) &&
                commitMessageLog.stream()
                        .filter(message -> message.getSequence() == sequence).count() >= config.getFaultThreshold();
    }
}
