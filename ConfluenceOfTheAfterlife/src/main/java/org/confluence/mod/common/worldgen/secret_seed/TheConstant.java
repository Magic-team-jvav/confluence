package org.confluence.mod.common.worldgen.secret_seed;

public class TheConstant extends SecretSeed {
    TheConstant(long flag) {
        super(flag);
    }

    @Override
    public boolean match(String seed) {
        return "constant".equals(seed) || "theconstant".equals(seed) || "the constant".equals(seed) || "eye4aneye".equals(seed) || "eyeforaneye".equals(seed);
    }
}
