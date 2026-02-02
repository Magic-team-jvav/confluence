package org.confluence.terraentity.data.security;

import java.util.function.Supplier;

class SecurityKey implements java.io.Serializable, Supplier<byte[]> {
    byte[] keys;
    long mask;
    KeyGenerator generator;
    SecurityKey(){
        keys = new byte[128];
    }

    @Override
    public byte[] get() {
        return generator.get();
    }
}
