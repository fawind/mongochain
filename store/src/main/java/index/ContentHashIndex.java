package index;

import com.google.common.collect.ImmutableMap;
import model.Key;

public interface ContentHashIndex {

    /**
     * Add the content hash for the namespace and key to the index.
     *
     * @param key the key of the content hash
     * @param contentHash the content hash indexed by the namespace and key
     */
    void put(final Key key, String contentHash);

    /**
     * Get the content hash for the namespace and key.
     *
     * @param key the key of the content hash
     * @return the content hash indexed by the namespace and key or null if none is found
     */
    String get(final Key key);

    /**
     * Checks if the index contains a content hash for the given namespace and key.
     *
     * @param key the key of the content hash
     * @return weather the index contains the content hash for the namespace and key
     */
    boolean contains(final Key key);

    /**
     * For debugging only
     * @return copy of the index
     */
    ImmutableMap<Key, String> getIndexCopy();

    // TODO add method to persist index
}
