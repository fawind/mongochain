package cluster;

import io.reactivex.functions.Consumer;
import lombok.Builder;
import lombok.Data;
import model.Transaction;

@Data
@Builder
public class ActorConfiguration {
    private final String identity;
    private final Consumer<Transaction> onConsensus;
    private final int faultThreshold;
    private final int communityId;
    private final int finalCommunityId;
}
