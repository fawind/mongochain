package pubsub;

public interface PubSubService {

    /**
     * TODO: Rough outline of our pubsub service. Change this to work with IPFS pubsub:
     * https://github.com/ipfs/java-ipfs-api/blob/master/src/main/java/io/ipfs/api/IPFS.java#L225
     */

    /**
     * Publish the new content hash for the namespace and key.
     *
     * @param namespace the namespace of the content hash
     * @param key the key of the content hash
     * @param contentHash the inserted content hash to publish
     */
    void publish(String namespace, String key, String contentHash);

    // TODO: Specify this interface
    void subscribe();
}
