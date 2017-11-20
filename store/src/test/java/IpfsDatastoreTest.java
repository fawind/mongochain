import index.ContentHashIndex;
import index.InMemoryContentHashIndex;
import io.ipfs.api.IPFS;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import pubsub.PubSubService;
import storage.ContentAddressableStorage;

import javax.inject.Inject;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JukitoRunner.class)
public class IpfsDatastoreTest {

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(ContentHashIndex.class).to(InMemoryContentHashIndex.class);
            bind(PubSubService.class).toInstance(mock(PubSubService.class));
            bind(IPFS.class).toInstance(mock(IPFS.class));
        }
    }

    @Inject private IpfsDatastore ipfsDatastore;

    @Test
    public void givenKeyAndValue_whenAdd_thenRetrieveValueForKey(ContentAddressableStorage storage) throws IOException {
        // GIVEN
        final String namespace = "namespace";
        final String key = "key";
        final String value = "Test Value";
        final String contentHash = "myContentHash";

        when(storage.put(value)).thenReturn(contentHash);
        when(storage.cat(contentHash)).thenReturn(value);

        // WHEN
        ipfsDatastore.add(namespace, key, value);
        String savedValue = ipfsDatastore.get(namespace, key);

        assertThat(savedValue).isEqualTo(value);
    }
}
