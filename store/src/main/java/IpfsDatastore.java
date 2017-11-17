import index.ContentHashIndex;
import pubsub.PubSubService;
import storage.ContentAddressableStorage;

import javax.inject.Inject;

public class IpfsDatastore implements Datastore {

    private final ContentAddressableStorage storage;
    private final ContentHashIndex index;
    private final PubSubService pubSubService;

    @Inject
    public IpfsDatastore(
            ContentAddressableStorage storage,
            ContentHashIndex index,
            PubSubService pubSubService) {
        this.storage = storage;
        this.index = index;
        this.pubSubService = pubSubService;
    }

    @Override
    public void add(String namespace, String key, String value) {
        String contentHash = this.storage.put(value);
        index.put(namespace, key, contentHash);
        pubSubService.publish(namespace, key, contentHash);
    }

    @Override
    public String get(String namespace, String key) {
        if (!index.contains(namespace, key)) {
            return null;
        }
        String contentHash = index.get(namespace, key);
        return storage.cat(contentHash);
    }
}
