package pubsub;

import io.reactivex.Observable;
import model.TransactionMessage;

import java.io.IOException;
import java.util.function.Function;

public interface PubSubService {
    /**
     * Publish the new content hash for the namespace and key.
     *
     * @param namespace the namespace of the content hash
     * @param key the key of the content hash
     * @param contentHash the inserted content hash to publish
     */
    void publish(String topic, String namespace, String key, String contentHash) throws IOException;

    Observable<TransactionMessage> observe(String topic) throws IOException;
}
