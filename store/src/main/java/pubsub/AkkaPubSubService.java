package pubsub;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

import java.io.Serializable;

public class AkkaPubSubService {
    
    private final ActorRef actor;
    private final ActorRef pubsubMediator;
    
    public AkkaPubSubService(ActorRef actor, ActorSystem system) {
        this.pubsubMediator = DistributedPubSub.get(system).mediator();
        this.actor = actor;
    }
    
    public void observe(String topic) {
        pubsubMediator.tell(new DistributedPubSubMediator.Subscribe(topic, actor), actor);
    }
    
    public void publish(String topic, Serializable msg) {
        pubsubMediator.tell(new DistributedPubSubMediator.Publish(topic, msg), actor);
    }
}
