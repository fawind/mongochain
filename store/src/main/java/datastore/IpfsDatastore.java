package datastore;

import index.ContentHashIndex;
import pubsub.PubSubService;
import storage.ContentAddressableStorage;

import javax.inject.Inject;
import java.io.IOException;
import java.util.logging.Logger;

import static configuration.DatastoreModule.PubsubTopic;
import static java.lang.String.format;


public class IpfsDatastore implements Datastore {

    private static Logger log = Logger.getLogger(IpfsDatastore.class.getName());

    private final ContentAddressableStorage storage;
    private final ContentHashIndex index;
    private final PubSubService pubSubService;
    private final String topic;
    
    @Inject
    public IpfsDatastore(
            ContentAddressableStorage storage,
            ContentHashIndex index,
            PubSubService pubSubService,
            @PubsubTopic String topic) {
        this.topic = topic;
        this.storage = storage;
        this.index = index;
        this.pubSubService = pubSubService;

        listenToIndexUpdates();
    }

    @Override
    public void add(String namespace, String key, String value) {
        try {
            String contentHash = this.storage.put(value);
            index.put(namespace, key, contentHash);
            pubSubService.publish(topic, namespace, key, contentHash);
            log.info(format("ADD: %s/%s: %s", namespace, key, value));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO handle failure of storage.put
        }
    }

    @Override
    public String get(String namespace, String key) {
        if (!index.contains(namespace, key)) {
            log.info(format("GET: %s/%s: null", namespace, key));
            return null;
        }
        String contentHash = index.get(namespace, key);
        try {
            String value = storage.cat(contentHash);
            log.info(format("GET: %s/%s: %s", namespace, key, value));
            return value;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void listenToIndexUpdates() {
        try {
            pubSubService.observe(topic).subscribe(transaction -> {
                index.put(transaction.getNamespace(), transaction.getKey(), transaction.getContentHash());
                log.info(format("SUB: %s/%s: %s", transaction.getNamespace(), transaction.getKey(), transaction.getContentHash()));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
