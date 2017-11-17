package storage;

public interface ContentAddressableStorage {

    /**
     * Adds the content to the storage.
     *
     * @param content the content to add to the storage
     * @return the content hash used to address the content
     */
    String put(final String content);

    /**
     * Returns the content of the given content hash.
     *
     * @param contentHash the content hash of the content to retrieve
     * @return the content addressed by the content hash
     */
    String cat(final String contentHash);
}
