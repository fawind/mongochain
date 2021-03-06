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
public class NewTransactionMessage implements Serializable {
    private Transaction transaction;
    private String identity;
    private boolean isFinal;
}
