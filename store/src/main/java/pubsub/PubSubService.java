package pubsub;

import java.io.IOException;

public interface PubSubService {

    /**
     * Publish the new content hash for the namespace and key.
     *
     * @param namespace the namespace of the content hash
     * @param key the key of the content hash
     * @param contentHash the inserted content hash to publish
     */
    void publish(String namespace, String key, String contentHash) throws IOException;

    void subscribe() throws IOException;
    
    String retrieveData();
}
