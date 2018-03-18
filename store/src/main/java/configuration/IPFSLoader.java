package configuration;

import io.ipfs.api.IPFS;

public class IPFSLoader {

    private static final int PORT = 5001;
    private static final String LOCAL_IP = "127.0.0.1";
    private static final String DOCKER_IP = "ipfs";
    private static final String VERSION = "/api/v0/";
    private static IPFS IPFS_INSTANCE;

    public IPFS getIPFS(boolean isDockerEnv) {
        if (IPFS_INSTANCE == null) {
            initializeIPFS(isDockerEnv);
        }
        return IPFS_INSTANCE;
    }

    private void initializeIPFS(boolean isDockerEnv) {
        if (isDockerEnv) {
            IPFS_INSTANCE = new IPFS(DOCKER_IP, PORT, VERSION);
        } else {
            IPFS_INSTANCE = new IPFS(LOCAL_IP, PORT, VERSION);
        }
    }
}
