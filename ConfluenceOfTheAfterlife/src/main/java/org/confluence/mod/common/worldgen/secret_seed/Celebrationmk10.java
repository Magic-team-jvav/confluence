package org.confluence.mod.common.worldgen.secret_seed;

public class Celebrationmk10 extends SecretSeed {
    Celebrationmk10(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "5162011".equals(seed) || "5162021".equals(seed) || "05162011".equals(seed) || "05162021".equals(seed) || "celebrationmk10".equals(seed);
    }
}
