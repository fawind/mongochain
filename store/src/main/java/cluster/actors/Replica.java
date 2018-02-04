package cluster.actors;

import akka.cluster.pubsub.DistributedPubSubMediator.SubscribeAck;
import cluster.ActorConfiguration;
import cluster.messages.CommitMessage;
import cluster.messages.LocalResultMessage;
import cluster.messages.PrepareMessage;
import cluster.messages.PreprepareMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static cluster.actors.Primary.PRIMARY_TOPIC;
import static cluster.logging.Event.logEvent;
import static cluster.logging.EventType.REPLICA_BROADCAST_RESULT;
import static cluster.logging.EventType.REPLICA_CONSENSUS_RESULT;
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
    private final List<LocalResultMessage> resultLog = new ArrayList<>();
    private final PriorityQueue<LocalResultMessage> pendingResults =
            new PriorityQueue<>(10, Comparator.comparingInt(LocalResultMessage::getSequence));
    private final ActorConfiguration config;

    public Replica(ActorConfiguration config) {
        this.config = config;
        observe(REPLICA_TOPIC, config.getCommunityId());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PreprepareMessage.class, this::handlePreprepare)
                .match(PrepareMessage.class, this::handlePrepare)
                .match(CommitMessage.class, this::handleCommit)
                .match(SubscribeAck.class, this::handleSubscribeAck)
                .matchAny(m -> log().info("{} received unknown message: {}", getClass().getName(), m))
                .build();
    }

    private void handlePreprepare(PreprepareMessage preprepare) {
        PrepareMessage prepare = PrepareMessage.fromPreprepare(preprepare);
        preprepareMessageLog.add(preprepare);
        prepareMessageLog.add(prepare);
        log().info(logEvent(REPLICA_NEW_PREPARE, prepare, getSelf()));
        publish(REPLICA_TOPIC, config.getCommunityId(), prepare);
    }

    private void handlePrepare(PrepareMessage prepare) {
        prepareMessageLog.add(prepare);
        log().info(logEvent(REPLICA_RECEIVE_PREPARE, prepare, getSelf()));
        if (isPrepared(prepare.getSequence())) {
            CommitMessage commit = CommitMessage.fromPreprepare(prepare);
            commitMessageLog.add(commit);
            log().info(logEvent(REPLICA_NEW_COMMIT, commit,getSelf()));
            publish(REPLICA_TOPIC, config.getCommunityId(), commit);
        }
    }

    private void handleCommit(CommitMessage commit) {
        log().info(logEvent(REPLICA_RECEIVE_COMMIT, commit, getSelf()));
        commitMessageLog.add(commit);
        if (isCommittedLocally(commit.getSequence())) {
            LocalResultMessage result = LocalResultMessage.fromCommit(commit);
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
    
    private void processValidatedResult(LocalResultMessage result) {
        log().info(logEvent(REPLICA_CONSENSUS_RESULT, result, getSelf()));
        if (!resultLog.contains(result)) {
            log().info(logEvent(REPLICA_BROADCAST_RESULT, result, getSelf()));
            publish(PRIMARY_TOPIC, config.getCommunityId(), result);
        }
        resultLog.add(result);
        // TODO: Save message timestamp for new min timestamps for filtering new transactions
    }
    
    private void processPendingResults() {
        while (!pendingResults.isEmpty() && isNextResult(pendingResults.peek())) {
            LocalResultMessage result = pendingResults.poll();
            processValidatedResult(result);
        }
    }
    
    private boolean isPrepared(int sequence) {
        // TODO: Use counter for message log
        return preprepareMessageLog.stream()
                .filter(message -> message.getSequence() == sequence).count() >= config.getFaultThreshold();
    }
    
    private boolean isNextResult(LocalResultMessage result) {
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
