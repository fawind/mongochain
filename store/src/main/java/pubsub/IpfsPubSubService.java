package pubsub;

import com.google.inject.Inject;
import io.ipfs.api.IPFS;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import model.TransactionMessage;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class IpfsPubSubService implements PubSubService {

    private static Logger log = Logger.getLogger(IpfsPubSubService.class.getName());

    private final IPFS.Pubsub pubsub;
    private Supplier<Object> supplier;

    @Inject
    public IpfsPubSubService(IPFS ipfs) {
        this.pubsub = ipfs.pubsub;
    }

    @Override
    public void publish(final String topic, final String namespace, final String key, final String contentHash)
            throws IOException {
        TransactionMessage transactionMessage = new TransactionMessage(namespace, key, contentHash);
        String message = serializeToString(transactionMessage);
        pubsub.pub(topic, message);
    }

    @Override
    public Observable<TransactionMessage> observe(String topic) throws IOException {
        supplier = pubsub.sub(topic);
        // Poll supplier due to empty map initialization
        supplier.get();
        return Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                .map(tick -> getMessage())
                .doOnError(error -> log.severe(error.toString()))
                .retry();
    }

    private TransactionMessage getMessage() throws UnsupportedEncodingException {
        Map<String, String> messageObject = (Map<String, String>) supplier.get();
        String message = messageObject.get("data");
        return (TransactionMessage) deserializeFromString(message);
    }

    private String serializeToString(Serializable obj) throws UnsupportedEncodingException {
        String serialized = new String(Base64.getEncoder().encode(SerializationUtils.serialize(obj)));
        return URLEncoder.encode(serialized, UTF_8.name());
    }

    private Serializable deserializeFromString(String message) throws UnsupportedEncodingException {
        String decoded = new String(Base64.getDecoder().decode(URLDecoder.decode(message, UTF_8.name())));
        return SerializationUtils.deserialize(Base64.getDecoder().decode(decoded));
    }
}
