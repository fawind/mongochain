package pubsub;

import com.google.inject.Inject;

import static configuration.DatastoreModule.PubsubTopic;
import static java.nio.charset.StandardCharsets.UTF_8;

import io.ipfs.api.IPFS;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import model.TransactionMessage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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
        String msg = new TransactionMessage(namespace, key, contentHash).serializeToString();
        pubsub.pub(topic, URLEncoder.encode(msg, UTF_8.name()));
    }

    @Override
    public Observable<TransactionMessage> observe() throws IOException {
        supplier = pubsub.sub(topic);
        
        /**
         * Poll supplier due to empty map initialization
         */
        supplier.get();
        return Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                .map(tick -> getMessage())
                .doOnError(System.out::println)
                .retry();
    }

    private TransactionMessage getMessage() throws UnsupportedEncodingException {
        Map<String, String> messageObject = (Map<String, String>) supplier.get();
        String decodedMessage = new String(Base64.getDecoder().decode(
                URLDecoder.decode(messageObject.get("data"), UTF_8.name())));
        return TransactionMessage.deserializeFromString(decodedMessage);
    }
}
