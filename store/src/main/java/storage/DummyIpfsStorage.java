package storage;

import com.google.common.hash.Hashing;

import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.String.format;

public class DummyIpfsStorage implements ContentAddressableStorage {

    @Override
    public String put(String content) {
        return Hashing.sha256().hashString(content, Charset.defaultCharset()).toString();
    }

    @Override
    public String cat(String contentHash) {
        return format("Dummy content for hash %s", contentHash);
    }
}
