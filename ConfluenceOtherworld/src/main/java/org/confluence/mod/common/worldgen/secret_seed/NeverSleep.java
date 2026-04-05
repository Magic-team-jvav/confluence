package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.resources.ResourceLocation;

public class NeverSleep extends SecretSeed {
    public NeverSleep(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "never sleep".equals(seed);
    }

    @Override
    public boolean isHided() {
        return true;
    }
}
