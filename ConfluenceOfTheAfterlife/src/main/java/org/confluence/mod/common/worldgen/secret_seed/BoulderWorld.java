package org.confluence.mod.common.worldgen.secret_seed;

public class BoulderWorld extends SecretSeed {
    public BoulderWorld(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "boulder".equals(seed);
    }
}
