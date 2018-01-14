package cluster.messages;

import lombok.Data;
import model.Identity;
import model.Transaction;

import java.io.Serializable;

@Data
public class PreprepareMessage implements Serializable {
    private final int sequence;
    private final Transaction transaction;
    private final Identity identity;
}
