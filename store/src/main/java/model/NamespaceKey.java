package model;

import java.util.Objects;

public class NamespaceKey {

    private final String namespace;
    private final String key;

    public NamespaceKey(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamespaceKey that = (NamespaceKey) o;
        return Objects.equals(namespace, that.namespace) &&
                Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, key);
    }

    @Override
    public String toString() {
        return "NamespaceKey{" +
                "namespace='" + namespace + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
