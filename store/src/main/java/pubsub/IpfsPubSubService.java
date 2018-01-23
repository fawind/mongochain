package pubsub;

import com.google.inject.Inject;
import io.ipfs.api.IPFS;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import model.Key;
import model.Transaction;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;

public class IpfsPubSubService implements PubSubService {

    private static final Logger log = LoggerFactory.getLogger(IpfsPubSubService.class);

    private final IPFS.Pubsub pubsub;
    private Supplier<Object> supplier;

    @Inject
    public IpfsPubSubService(IPFS ipfs) {
        this.pubsub = ipfs.pubsub;
    }

    @Override
    public void publish(final String topic, final Key key, final String contentHash)
            throws IOException {
        Transaction transactionMessage = new Transaction(key, contentHash);
        String message = serializeToString(transactionMessage);
        pubsub.pub(topic, message);
    }

    @Override
    public Observable<Transaction> observe(String topic) throws IOException {
        supplier = pubsub.sub(topic);
        // Poll supplier due to empty map initialization
        supplier.get();
        return Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                .map(tick -> getMessage())
                .doOnError(error -> log.error(error.toString()))
                .retry();
    }

    private Transaction getMessage() throws UnsupportedEncodingException {
        Map<String, String> messageObject = (Map<String, String>) supplier.get();
        String message = messageObject.get("data");
        return (Transaction) deserializeFromString(message);
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
