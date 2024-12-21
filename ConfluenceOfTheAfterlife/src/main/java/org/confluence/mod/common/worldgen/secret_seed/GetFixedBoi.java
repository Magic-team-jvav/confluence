package org.confluence.mod.common.worldgen.secret_seed;

public class GetFixedBoi extends SecretSeed {
    GetFixedBoi(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "getfixedboi".equals(seed) || "get fixed boi".equals(seed);
    }
}
