package org.confluence.mod.common.worldgen.secret_seed;

public class DrunkWorld extends SecretSeed {
    DrunkWorld(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "5162020".equals(seed) || "05162020".equals(seed);
    }
}
