package cluster.logging;

import akka.actor.ActorRef;
import lombok.Data;

import static java.lang.String.format;

@Data
public class Event {

    public static String logEvent(EventType type, Object payload, ActorRef actor) {
        return new Event(actor, type, payload).toLogString();
    }

    private final ActorRef actor;
    private final EventType type;
    private final Object payload;

    private String toLogString() {
        return format("Event:%s Actor:%s %s", type, actor, payload);
    }
}
