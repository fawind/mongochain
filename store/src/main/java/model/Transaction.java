package model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Transaction implements Serializable {
    private final Key key;
    private final String contentHash;
}
