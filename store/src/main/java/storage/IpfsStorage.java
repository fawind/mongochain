package storage;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * TODO: Implement interface using ipfs.
 */
public class IpfsStorage implements ContentAddressableStorage {

    @Override
    public String put(String content) {
        throw new NotImplementedException();
    }

    @Override
    public String cat(String contentHash) {
        throw new NotImplementedException();
    }
}
