package datastore;

import model.SecuredTransaction;
import model.Transaction;

public class SecuredTransactionLog {

    private SecuredTransaction head = new SecuredTransaction(null, null);

    public void addTransaction(Transaction transaction) {
        head = new SecuredTransaction(transaction, head);
    }

    public SecuredTransaction getHead() {
        return head;
    }
}
