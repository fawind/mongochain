package model;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TransactionMessage implements Serializable {

    public static TransactionMessage deserializeFromString(final String encoded) throws UnsupportedEncodingException {
        return SerializationUtils.deserialize(Base64.getDecoder().decode(encoded));
    }

    private final String namespace;
    private final String key;
    private final String contentHash;

    public TransactionMessage(String namespace, String key, String contentHash) {
        this.namespace = namespace;
        this.key = key;
        this.contentHash = contentHash;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    public String getContentHash() {
        return contentHash;
    }

    public String serializeToString() throws UnsupportedEncodingException {
        return new String(Base64.getEncoder().encode(SerializationUtils.serialize(this)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionMessage that = (TransactionMessage) o;
        return Objects.equals(namespace, that.namespace) &&
                Objects.equals(key, that.key) &&
                Objects.equals(contentHash, that.contentHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, key, contentHash);
    }

    @Override
    public String toString() {
        return "TransactionMessage{" +
                "namespace='" + namespace + '\'' +
                ", key='" + key + '\'' +
                ", contentHash='" + contentHash + '\'' +
                '}';
    }
}
