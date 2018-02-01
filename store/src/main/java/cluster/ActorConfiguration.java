package cluster;

import io.reactivex.functions.Consumer;
import lombok.Builder;
import lombok.Data;
import model.Identity;
import model.Transaction;

@Data
@Builder
public class ActorConfiguration {
    private final String identity;
    private final Consumer<Transaction> onConsensus;
    private final int faultThreshold;
}
