package org.confluence.mod.common.worldgen.secret_seed;

public class ForTheWorthy extends SecretSeed {
    ForTheWorthy(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "fortheworthy".equals(seed) || "for the worthy".equals(seed);
    }
}
