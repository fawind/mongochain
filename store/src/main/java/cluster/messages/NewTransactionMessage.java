package cluster.messages;

import lombok.Data;
import model.Identity;
import model.Transaction;

import java.io.Serializable;

@Data
public class NewTransactionMessage implements Serializable {
    private final Transaction transaction;
    private final Identity identity;
}
