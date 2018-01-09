package configuration;

import io.ipfs.api.IPFS;

public class IPFSLoader {
   private static IPFS ipfs;
   private static final int port = 5001;
   private static final String version = "/api/v0/";
    
    public IPFS getIPFS() {
       if (ipfs == null) {
           initializeIPFS();
       }
       return ipfs;
   }
   
   private void initializeIPFS() {
       String env = System.getenv("STORE_ENV");
       String host;
       if (env != null && env.equals("docker")) {
           host = "ipfs";
       } else {
           host = "127.0.0.1";
       }
       ipfs = new IPFS(host, port, version);
   }
}
