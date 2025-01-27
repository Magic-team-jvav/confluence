package org.confluence.mod.common.worldgen.secret_seed;

public class DontDigUp extends SecretSeed {
    public DontDigUp(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "dontdigup".equals(seed) || "dont dig up".equals(seed) || "don't dig up".equals(seed);
    }
}
