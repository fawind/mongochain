package cluster.messages;

import lombok.Data;
import model.Identity;
import model.Transaction;

import java.io.Serializable;

@Data
public class ResultMessage implements Serializable {
    private final int sequence;
    private final Transaction transaction;
    private final Identity identity;

    public static ResultMessage fromCommit(CommitMessage commit) {
        return new ResultMessage(commit.getSequence(), commit.getTransaction(), commit.getIdentity());
    }
}
