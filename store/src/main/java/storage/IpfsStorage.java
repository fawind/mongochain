package storage;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

import javax.inject.Inject;
import java.io.IOException;

public class IpfsStorage implements ContentAddressableStorage {

    private final IPFS ipfs;

    @Inject
    public IpfsStorage(IPFS ipfs) {
        this.ipfs = ipfs;
    }

    @Override
    public String put(String content) throws IOException {
        final NamedStreamable.ByteArrayWrapper byteContent = new NamedStreamable.ByteArrayWrapper(content.getBytes());
        final MerkleNode addResult = ipfs.add(byteContent).get(0);
        return addResult.hash.toString();
    }

    @Override
    public String cat(String contentHash) throws IOException {
        final Multihash filePointer = Multihash.fromBase58(contentHash);
        final byte[] fileContents = ipfs.cat(filePointer);
        return new String(fileContents);
    }
}
