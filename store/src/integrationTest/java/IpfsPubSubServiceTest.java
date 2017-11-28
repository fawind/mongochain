import com.google.inject.Inject;
import io.ipfs.api.IPFS;
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
	public void givenTwoMessages_whenPublish_thenRetrieveBoth() throws IOException {
		// GIVEN
		pubsub.subscribe();
		String namespace = "namespace";
		String key = "key";
		String contentHash1 = "1";
		String contentHash2 = "2";

		// WHEN
		pubsub.publish(namespace, key, "1");
		pubsub.publish(namespace, key, "2");

		// THEN
		assertThat(pubsub.retrieveData()).isEqualTo(namespace + "|" + key + "|" + contentHash1);
		assertThat(pubsub.retrieveData()).isEqualTo(namespace + "|" + key + "|" + contentHash2);
	}
}
