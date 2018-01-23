package cluster;

import com.typesafe.config.Config;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsensusServiceConfiguration {
    private final int port;
    private final int faultThreshold;
    private final boolean isPrimary;
    private final Config akkaConfig;
}
