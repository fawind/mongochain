package model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TransactionMessage implements Serializable {

    private final String namespace;
    private final String key;
    private final String contentHash;
}
