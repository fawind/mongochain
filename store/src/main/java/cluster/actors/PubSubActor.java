package cluster.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

import java.io.Serializable;

public abstract class PubSubActor extends AbstractLoggingActor {
    
    private final ActorRef pubsubMediator = DistributedPubSub.get(getContext().getSystem()).mediator();


    void observe(String topic) {
        pubsubMediator.tell(new DistributedPubSubMediator.Subscribe(topic, getSelf()), getSelf());
    }

    void publish(String topic, Serializable msg) {
        pubsubMediator.tell(new DistributedPubSubMediator.Publish(topic, msg), getSelf());
    }
}
