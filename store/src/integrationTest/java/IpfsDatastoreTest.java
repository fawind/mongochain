import index.ContentHashIndex;
import index.InMemoryContentHashIndex;
import io.ipfs.api.IPFS;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import storage.ContentAddressableStorage;
import storage.IpfsStorage;

import javax.inject.Inject;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JukitoRunner.class)
public class IpfsDatastoreTest {

    private static final String LOCAL_IPFS_ADDRESS = "/ip4/127.0.0.1/tcp/5001";

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(ContentHashIndex.class).to(InMemoryContentHashIndex.class);
            bind(ContentAddressableStorage.class).to(IpfsStorage.class);
            bind(IPFS.class).toInstance(new IPFS(LOCAL_IPFS_ADDRESS));
        }
    }

    @Inject private IpfsDatastore ipfsDatastore;

    @Test
    public void givenKeyAndValue_whenAdd_thenRetrieveValueForKey() {
        // GIVEN
        final String namespace = "namespace";
        final String key = "key";
        final Random randomGenerator = new Random();
        final String value = String.valueOf(randomGenerator.nextInt(1000));

        // WHEN
        ipfsDatastore.add(namespace, key, value);
        String savedValue = ipfsDatastore.get(namespace, key);

        assertThat(savedValue).isEqualTo(value);
    }
}
