package model;

import lombok.Data;

@Data
public class SecuredTransaction {
    private final Transaction transaction;
    private final SecuredTransaction predecessor;
}
