package cluster.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

import java.io.Serializable;

import static java.lang.String.format;

public abstract class PubSubActor extends AbstractLoggingActor {
    
    private final ActorRef pubsubMediator = DistributedPubSub.get(getContext().getSystem()).mediator();

    void handleSubscribeAck(DistributedPubSubMediator.SubscribeAck subscribeAck) {
        log().info("{} Subscribed to {}", getSelf(), subscribeAck);
    }

    void observe(String topic) {
        pubsubMediator.tell(new DistributedPubSubMediator.Subscribe(topic, getSelf()), getSelf());
    }

    void observe(String topic, int communityId) {
        pubsubMediator.tell(new DistributedPubSubMediator.Subscribe(getTopic(topic, communityId), getSelf()), getSelf());
    }

    void publish(String topic, Serializable msg) {
        pubsubMediator.tell(new DistributedPubSubMediator.Publish(topic, msg), getSelf());
    }

    void publish(String topic, int communityId, Serializable msg) {
        pubsubMediator.tell(new DistributedPubSubMediator.Publish(getTopic(topic, communityId), msg), getSelf());
    }

    private String getTopic(String topic, int communityId) {
        return format("%s-%d", topic, communityId);
    }
}
