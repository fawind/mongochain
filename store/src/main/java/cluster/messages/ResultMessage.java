package cluster.messages;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.Identity;
import model.Transaction;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.NONE)
public class ResultMessage implements Serializable {
    private int sequence;
    private Transaction transaction;
    private String identity;

    public static ResultMessage fromCommit(CommitMessage commit) {
        return new ResultMessage(commit.getSequence(), commit.getTransaction(), commit.getIdentity());
    }
}
