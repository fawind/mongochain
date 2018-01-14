import cluster.ConsensusService;
import datastore.IpfsDatastore;
import datastore.SecuredTransactionLog;
import datastore.TransactionBacklog;
import index.ContentHashIndex;
import index.InMemoryContentHashIndex;
import model.DatastoreException;
import model.Key;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import storage.ContentAddressableStorage;

import javax.inject.Inject;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(JukitoRunner.class)
public class IpfsDatastoreTest {

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(ContentHashIndex.class).to(InMemoryContentHashIndex.class);
            bind(TransactionBacklog.class).toInstance(mock(TransactionBacklog.class));
            bind(SecuredTransactionLog.class).toInstance(mock(SecuredTransactionLog.class));
            bind(ConsensusService.class).toInstance(mock(ConsensusService.class));
        }
    }

    @Inject private IpfsDatastore ipfsDatastore;

    @Test
    public void givenKeyAndValue_whenAdd_thenAddKeyToBacklog(
            ContentAddressableStorage storage, TransactionBacklog transactionBacklog) throws DatastoreException, IOException {
        // GIVEN
        final Key key = new Key("namespace", "key");
        final String value = "Test Value";
        final String contentHash = "myContentHash";

        when(storage.put(value)).thenReturn(contentHash);

        // WHEN
        ipfsDatastore.add(key, value);

        // THEN
        verify(transactionBacklog, times(1)).addTransaction(any());
    }
}
