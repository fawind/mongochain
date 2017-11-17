package index;

public interface ContentHashIndex {

    /**
     * Add the content hash for the namespace and key to the index.
     *
     * @param namespace the namespace of the content hash
     * @param key the key of the content hash
     * @param contentHash the content hash indexed by the namespace and key
     */
    void put(final String namespace, final String key, String contentHash);

    /**
     * Get the content hash for the namespace and key.
     *
     * @param namespace the namespace of the content hash
     * @param key the key of the content hash
     * @return the content hash indexed by the namespace and key or null if none is found
     */
    String get(final String namespace, final String key);

    /**
     * Checks if the index contains a content hash for the given namespace and key.
     *
     * @param namespace the namsepace of the content hash
     * @param key the key of the content hash
     * @return weather the index contains the content hash for the namespace and key
     */
    boolean contains(final String namespace, final String key);

    // TODO add method to persist index
}
