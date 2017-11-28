package pubsub;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import io.ipfs.api.IPFS;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.function.Supplier;

import static configuration.DatastoreModule.PubsubTopic;

public class IpfsPubSubService implements PubSubService {

    private final IPFS.Pubsub pubsub;
    private final String topic;
    private Supplier<Object> supplier;

    @Inject
    public IpfsPubSubService(IPFS ipfs, @PubsubTopic String topic) {
        this.pubsub = ipfs.pubsub;
        this.topic = topic;
    }

    @Override
    public void publish(final String namespace, final String key, final String contentHash) throws IOException {
        pubsub.pub(topic, String.join("|", ImmutableList.of(namespace, key, contentHash)));
    }

    @Override
    public void subscribe() throws IOException {
        supplier = pubsub.sub(topic);
        
        /**
         * Poll supplier due to empty map initialization
         */
        supplier.get();
    }

    @Override
    public String retrieveData() {
        Map<String, String> message = retrieveMessage(topic);
        return new String(Base64.getDecoder().decode(message.get("data")));
    }

    private Map<String, String> retrieveMessage(final String topic) {
        return (Map<String, String>) supplier.get();
    }
}
