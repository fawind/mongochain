package model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class Identity implements Serializable {

    private final UUID id;

    public Identity() {
        this.id = UUID.randomUUID();
    }
}
