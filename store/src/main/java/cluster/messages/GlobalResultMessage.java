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
public class GlobalResultMessage implements Serializable {
    private int sequence;
    private Transaction transaction;
    private String identity;

    public static GlobalResultMessage fromLocalResult(LocalResultMessage message) {
        return new GlobalResultMessage(
                message.getSequence(),
                message.getTransaction(),
                message.getIdentity());
    }
}
