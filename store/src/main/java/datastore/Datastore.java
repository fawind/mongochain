package datastore;

import model.DatastoreException;
import model.Key;

public interface Datastore {

    /**
     * Adds the value to the store addressed by its namespace and key.
     *
     * @param key the key identifying the value
     * @param value the value to add to the store
     */
    void add(final Key key, final String value) throws DatastoreException;

    /**
     * Retrieve the value for the given namespace and key.
     *
     * @param key the key identifying the value
     * @return the value for the given namespace and key or null if no value exists
     */
    String get(final Key key) throws DatastoreException;
}
