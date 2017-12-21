import com.google.inject.Inject;
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
import java.io.UnsupportedEncodingException;

import static configuration.DatastoreModule.PubsubTopic;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JukitoRunner.class)
public class IpfsPubSubServiceTest {

	private static final String LOCAL_IPFS_ADDRESS = "/ip4/127.0.0.1/tcp/5001";
	private static final String PUBSUB_TOPIC = "ipfs-test-topic";

	public static class Module extends JukitoModule {
		@Override
		protected void configureTest() {
			bind(IPFS.class).toInstance(new IPFS(LOCAL_IPFS_ADDRESS));
			bindConstant().annotatedWith(PubsubTopic.class).to(PUBSUB_TOPIC);
		}
	}

	@Inject
	private IpfsPubSubService pubsub;

	@Test
    public void testSerialization() throws UnsupportedEncodingException {
        TransactionMessage msg = new TransactionMessage("ns", "key", "chash");
        String encoded = msg.serializeToString();
        TransactionMessage decoded = TransactionMessage.deserializeFromString(encoded);

        assertThat(decoded).isEqualTo(msg);
    }

	@Test
	public void givenTwoMessages_whenPublish_thenRetrieveBoth() throws IOException, InterruptedException {
		// GIVEN
        TestObserver<TransactionMessage> observer = new TestObserver<>();
		String namespace = "namespace";
		String key = "key";
		String contentHash1 = "1";
		String contentHash2 = "2";



		// WHEN
        Observable<TransactionMessage> observable = pubsub.observe();
        observable.subscribe(observer);

		pubsub.publish(namespace, key, contentHash1);
        pubsub.publish(namespace, key, contentHash2);

		// THEN
        observer.awaitCount(2);
        observer.assertNoErrors();
        assertThat(observer.values().get(0))
                .isEqualTo(new TransactionMessage(namespace, key, contentHash1));
        assertThat(observer.values().get(1))
                .isEqualTo(new TransactionMessage(namespace, key, contentHash2));
	}
}
