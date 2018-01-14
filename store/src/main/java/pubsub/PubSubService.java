package pubsub;

import io.reactivex.Observable;
import model.Key;
import model.Transaction;

import java.io.IOException;

public interface PubSubService {
    /**
     * Publish the new content hash for the namespace and key.
     *
     * @param key the key of the content hash
     * @param contentHash the inserted content hash to publish
     */
    void publish(String topic, Key key, String contentHash) throws IOException;

    Observable<Transaction> observe(String topic) throws IOException;
}
