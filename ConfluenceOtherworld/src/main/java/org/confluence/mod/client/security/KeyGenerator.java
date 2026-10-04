package org.confluence.mod.client.security;

import java.io.Serializable;
import java.util.function.Supplier;

class KeyGenerator implements Supplier<byte[]>, Serializable {
    byte[] key;

    @Override
    public byte[] get() {
        return key;
    }
}
