package org.confluence.mod.common.worldgen.secret_seed;

public class NoTraps extends SecretSeed {
    public NoTraps(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "notraps".equals(seed) || "no traps".equals(seed);
    }
}
