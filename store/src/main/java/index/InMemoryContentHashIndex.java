package index;

import com.google.common.collect.ImmutableMap;
import model.Key;

import java.util.HashMap;
import java.util.Map;

public class InMemoryContentHashIndex implements ContentHashIndex {

    private final Map<Key, String> index = new HashMap<>();

    @Override
    public void put(Key key, String contentHash) {
        index.put(key, contentHash);
    }

    @Override
    public String get(Key key) {
        return index.get(key);
    }

    @Override
    public boolean contains(Key key) {
        return index.containsKey(key);
    }

    @Override
    public ImmutableMap<Key, String> getIndexCopy() {
        return ImmutableMap.copyOf(index);
    }
}
