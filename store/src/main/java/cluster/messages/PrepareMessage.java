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
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.NONE)
public class PrepareMessage implements Serializable {
    private int sequence;
    private Transaction transaction;
    private String identity;

    public static PrepareMessage fromPreprepare(PreprepareMessage preprepare) {
        return new PrepareMessage(preprepare.getSequence(), preprepare.getTransaction(), preprepare.getIdentity());
    }
}
