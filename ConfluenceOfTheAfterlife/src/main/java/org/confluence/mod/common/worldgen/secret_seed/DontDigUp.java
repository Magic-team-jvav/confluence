package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.resources.ResourceLocation;

public class DontDigUp extends SecretSeed {
    public DontDigUp(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "dontdigup".equals(seed) || "dont dig up".equals(seed) || "don't dig up".equals(seed);
    }
}
