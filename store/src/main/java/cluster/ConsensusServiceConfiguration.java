package cluster;

import com.typesafe.config.Config;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString(exclude="akkaConfig")
public class ConsensusServiceConfiguration {
    private final String identity;
    private final int faultThreshold;
    private final boolean isPrimary;
    private final int communityId;
    private final int finalCommunityId;
    private final Config akkaConfig;
    // Dev only
    private final boolean runLocally;
}
