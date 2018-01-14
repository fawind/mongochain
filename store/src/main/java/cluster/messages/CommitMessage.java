package cluster.messages;

import lombok.Data;
import model.Identity;
import model.Transaction;

import java.io.Serializable;

@Data
public class CommitMessage implements Serializable {
    private final int sequence;
    private final Transaction transaction;
    private final Identity identity;

    public static CommitMessage fromPreprepare(PrepareMessage prepare) {
        return new CommitMessage(prepare.getSequence(), prepare.getTransaction(), prepare.getIdentity());
    }
}
