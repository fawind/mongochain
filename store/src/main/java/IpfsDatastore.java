import index.ContentHashIndex;
import pubsub.PubSubService;
import storage.ContentAddressableStorage;

import javax.inject.Inject;
import java.io.IOException;

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
        try {
            String contentHash = this.storage.put(value);
            index.put(namespace, key, contentHash);
            pubSubService.publish(namespace, key, contentHash);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO handle failure of storage.put
        }

    }

    @Override
    public String get(String namespace, String key) {
        if (!index.contains(namespace, key)) {
            // TODO handle namespace->key not found in index
            return null;
        }
        String contentHash = index.get(namespace, key);
        try {
            return storage.cat(contentHash);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO handle failure of storage.get
            return "";
        }
    }
}
