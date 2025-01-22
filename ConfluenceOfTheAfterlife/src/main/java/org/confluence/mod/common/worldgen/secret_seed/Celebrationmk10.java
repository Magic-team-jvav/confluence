package org.confluence.mod.common.worldgen.secret_seed;

public class Celebrationmk10 extends SecretSeed {
    Celebrationmk10(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        try {
            return Long.parseLong(seed) == 5162011L || "celebrationmk10".equals(seed);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
