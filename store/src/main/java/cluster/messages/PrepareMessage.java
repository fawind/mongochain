package cluster.messages;

import lombok.Data;
import model.Identity;
import model.Transaction;

import java.io.Serializable;

@Data
public class PrepareMessage implements Serializable {
    private final int sequence;
    private final Transaction transaction;
    private final Identity identity;

    public static PrepareMessage fromPreprepare(PreprepareMessage preprepare) {
        return new PrepareMessage(preprepare.getSequence(), preprepare.getTransaction(), preprepare.getIdentity());
    }
}
