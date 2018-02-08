package datastore;

import model.SecuredTransaction;
import model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class SecuredTransactionLog {

    private final Set<Transaction> seen = new HashSet<>();

    private static final Logger log = LoggerFactory.getLogger(SecuredTransactionLog.class);

    private SecuredTransaction head = new SecuredTransaction(null, null);

    public void addTransaction(Transaction transaction) {
        // TODO: HACK: Local demo only
        if (seen.contains(transaction)) {
            return;
        }
        log.info("{}\n", transaction.toString());
        seen.add(transaction);
        head = new SecuredTransaction(transaction, head);
    }

    public SecuredTransaction getHead() {
        return head;
    }
}
