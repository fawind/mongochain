package datastore;

import model.Identity;

public class IdentityProvider {

    private final static Identity IDENTITY = new Identity();

    public Identity get() {
        return IDENTITY;
    }
}
