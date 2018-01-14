package model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Key implements Serializable {
    private final String namespace;
    private final String key;
}
