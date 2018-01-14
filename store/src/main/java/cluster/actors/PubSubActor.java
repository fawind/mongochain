package cluster.actors;

import akka.actor.AbstractLoggingActor;
import akka.cluster.Cluster;
import pubsub.AkkaPubSubService;

public abstract class PubSubActor extends AbstractLoggingActor {
    
    private final Cluster cluster = Cluster.get(getContext().system());
    final AkkaPubSubService pubsubService = new AkkaPubSubService(getSelf(), getContext().getSystem());
}
