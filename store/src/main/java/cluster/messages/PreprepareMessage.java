package cluster.messages;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.Transaction;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.NONE)
public class PreprepareMessage implements Serializable {
    private int sequence;
    private Transaction transaction;
    private String identity;
    private boolean isFinal;

    public static PreprepareMessage fromNewTransaction(int sequenceNumber, NewTransactionMessage message) {
        return new PreprepareMessage(
                sequenceNumber,
                message.getTransaction(),
                message.getIdentity(),
                message.isFinal());
    }
}
