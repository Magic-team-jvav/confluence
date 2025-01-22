package org.confluence.mod.common.worldgen.secret_seed;

public class DrunkWorld extends SecretSeed {
    DrunkWorld(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        try {
            return Long.parseLong(seed) == 5162020L;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
