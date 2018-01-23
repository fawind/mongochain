package configuration;

import io.ipfs.api.IPFS;

public class IPFSLoader {

   private static IPFS IPFS_INSTANCE;
   private static final int PORT = 5001;
   private static final String VERSION = "/api/v0/";
    
    public IPFS getIPFS(boolean isDockerEnv) {
       if (IPFS_INSTANCE == null) {
           initializeIPFS(isDockerEnv);
       }
       return IPFS_INSTANCE;
   }
   
   private void initializeIPFS(boolean isDockerEnv) {
       if (isDockerEnv) {
           IPFS_INSTANCE = new IPFS("ipfs", PORT, VERSION);
       } else {
           IPFS_INSTANCE = new IPFS("127.0.0.1", PORT, VERSION);
       }
   }
}
