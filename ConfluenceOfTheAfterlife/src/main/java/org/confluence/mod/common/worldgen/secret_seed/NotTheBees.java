package org.confluence.mod.common.worldgen.secret_seed;

public class NotTheBees extends SecretSeed {
    NotTheBees(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "notthebees".equals(seed) || "not the bees".equals(seed) || "not the bees!".equals(seed);
    }
}
