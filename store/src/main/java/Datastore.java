public interface Datastore {

    /**
     * Adds the value to the store addressed by its namespace and key.
     *
     * @param namespace the namespace identifying the value
     * @param key the key identifying the value
     * @param value the value to add to the store
     */
    void add(final String namespace, final String key, final String value);

    /**
     * Retrieve the value for the given namespace and key.
     *
     * @param namespace the namespace identifying the value
     * @param key the key identifying the value
     * @return the value for the given namespace and key or null if no value exists
     */
    String get(final String namespace, final String key);
}
