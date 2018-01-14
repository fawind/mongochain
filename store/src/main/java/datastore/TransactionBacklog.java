package datastore;

import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import model.Transaction;

public class TransactionBacklog {

    private final Subject<Transaction> transactionSubject = PublishSubject.create();

    public void addTransaction(Transaction transaction) {
        transactionSubject.onNext(transaction);
    }

    public void subscribe(Consumer<Transaction> consumer) {
        transactionSubject.subscribe(consumer);
    }
}
