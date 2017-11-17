package index;

import model.NamespaceKey;

import java.util.HashMap;

public class InMemoryContentHashIndex implements ContentHashIndex {

    private final HashMap<NamespaceKey, String> index;

    public InMemoryContentHashIndex() {
        this.index = new HashMap<>();
    }

    @Override
    public void put(String namespace, String key, String contentHash) {
        index.put(new NamespaceKey(namespace, key), contentHash);
    }

    @Override
    public String get(String namespace, String key) {
        return index.get(new NamespaceKey(namespace, key));
    }

    @Override
    public boolean contains(String namespace, String key) {
        return index.containsKey(new NamespaceKey(namespace, key));
    }
}
