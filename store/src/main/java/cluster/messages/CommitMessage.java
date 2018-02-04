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
public class CommitMessage implements Serializable {
    private int sequence;
    private Transaction transaction;
    private String identity;
    private boolean isFinal;

    public static CommitMessage fromPreprepare(PrepareMessage prepare) {
        return new CommitMessage(
                prepare.getSequence(),
                prepare.getTransaction(),
                prepare.getIdentity(),
                prepare.isFinal());
    }
}
