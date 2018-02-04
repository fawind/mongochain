package cluster.messages;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.Transaction;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.NONE)
public class LocalResultMessage implements Serializable {
    private int sequence;
    private Transaction transaction;
    private String identity;
    private boolean isFinal;

    public static LocalResultMessage fromCommit(CommitMessage commit) {
        return new LocalResultMessage(
                commit.getSequence(),
                commit.getTransaction(),
                commit.getIdentity(),
                commit.isFinal());
    }
}
