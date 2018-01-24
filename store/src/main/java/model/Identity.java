package model;

import lombok.Data;

import java.util.UUID;

@Data
public class Identity {

    private final UUID id;

    public Identity() {
        this.id = UUID.randomUUID();
    }

    public boolean isSameIdentity(String identity) {
        return this.id.toString().equals(identity);
    }
}
