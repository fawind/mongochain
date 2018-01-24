import cluster.ConsensusService;
import configuration.IPFSLoader;
import datastore.IpfsDatastore;
import datastore.TransactionBacklog;
import index.ContentHashIndex;
import index.InMemoryContentHashIndex;
import io.ipfs.api.IPFS;
import model.DatastoreException;
import model.Key;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import storage.ContentAddressableStorage;
import storage.IpfsStorage;

import javax.inject.Inject;
import java.util.Random;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JukitoRunner.class)
public class IpfsDatastoreTest {
    
    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(ContentHashIndex.class).to(InMemoryContentHashIndex.class);
            bind(ContentAddressableStorage.class).to(IpfsStorage.class);
            bind(IPFS.class).toInstance(new IPFSLoader().getIPFS(false));
            bind(TransactionBacklog.class).toInstance(mock(TransactionBacklog.class));
            bind(ConsensusService.class).toInstance(mock(ConsensusService.class));
        }
    }
    
    @Inject
    private IpfsDatastore ipfsDatastore;
    
    @Test
    public void givenKeyAndValue_whenAdd_thenRetrieveValueForKey(TransactionBacklog transactionBacklog) throws DatastoreException {
        // GIVEN
        final Key key = new Key("namespace", "key");
        final Random randomGenerator = new Random();
        final String value = String.valueOf(randomGenerator.nextInt(1000));
        
        // WHEN
        ipfsDatastore.add(key, value);

        // THEN
        verify(transactionBacklog, times(1)).addTransaction(any());
    }
}
