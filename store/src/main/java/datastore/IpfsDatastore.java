package datastore;

import cluster.ConsensusService;
import index.ContentHashIndex;
import model.DatastoreException;
import model.Key;
import model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.ContentAddressableStorage;

import javax.inject.Inject;
import java.io.IOException;

import static java.lang.String.format;


public class IpfsDatastore implements Datastore {



    private final ContentAddressableStorage storage;
    private final ContentHashIndex index;
    private final TransactionBacklog transactionBacklog;
    private final SecuredTransactionLog transactionLog;

    @Inject
    public IpfsDatastore(
            ContentAddressableStorage storage,
            ContentHashIndex index,
            TransactionBacklog transactionBacklog,
            ConsensusService consensusService,
            SecuredTransactionLog transactionLog) {
        this.storage = storage;
        this.index = index;
        this.transactionBacklog = transactionBacklog;
        this.transactionLog = transactionLog;
        consensusService.start(this::onConsensus);
    }

    @Override
    public void add(Key key, String value) throws DatastoreException {
        try {
            String contentHash = this.storage.put(value);
            transactionBacklog.addTransaction(new Transaction(key, contentHash));
        } catch (IOException e) {
            throw new DatastoreException(format("Error adding key, value: %s: %s", key, value), e);
        }
    }

    @Override
    public String get(Key key) throws DatastoreException {
        if (!index.contains(key)) {
            return null;
        }
        String contentHash = index.get(key);
        try {
            return storage.cat(contentHash);
        } catch (IOException e) {
            throw new DatastoreException(format("Error getting key %s", key), e);
        }
    }

    private void onConsensus(Transaction transaction) {
        transactionLog.addTransaction(transaction);
        index.put(transaction.getKey(), transaction.getContentHash());
    }
}
