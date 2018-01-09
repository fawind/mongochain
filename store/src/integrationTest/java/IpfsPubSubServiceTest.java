import com.google.inject.Inject;
import configuration.IPFSLoader;
import io.ipfs.api.IPFS;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import model.TransactionMessage;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import pubsub.IpfsPubSubService;

import java.io.IOException;

import static configuration.DatastoreModule.PubsubTopic;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JukitoRunner.class)
public class IpfsPubSubServiceTest {
    
    private static final String PUBSUB_TOPIC = "ipfs-test-topic";

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(IPFS.class).toInstance(new IPFSLoader().getIPFS());
            bindConstant().annotatedWith(PubsubTopic.class).to(PUBSUB_TOPIC);
        }
    }

    @Inject
    private IpfsPubSubService pubsub;

    @Test
    public void givenTwoMessages_whenPublish_thenRetrieveBoth() throws IOException {
        // GIVEN
        TestObserver<TransactionMessage> observer = new TestObserver<>();
        String namespace = "namespace";
        String key = "key";
        String contentHash1 = "1";
        String contentHash2 = "2";
        

        // WHEN
        String topic = "test";
        Observable<TransactionMessage> observable = pubsub.observe(topic);
        observable.subscribe(observer);

        pubsub.publish(topic, namespace, key, contentHash1);
        pubsub.publish(topic, namespace, key, contentHash2);

        // THEN
        observer.awaitCount(2);
        observer.assertNoErrors();
        assertThat(observer.values().get(0))
                .isEqualTo(new TransactionMessage(namespace, key, contentHash1));
        assertThat(observer.values().get(1))
                .isEqualTo(new TransactionMessage(namespace, key, contentHash2));
    }
}
