package model;

import java.io.Serializable;
import lombok.Data;

import java.util.UUID;

@Data
public class Identity implements Serializable {

    private final UUID id;

    public Identity() {
        this.id = UUID.randomUUID();
    }
}
