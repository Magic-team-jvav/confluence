package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.resources.ResourceLocation;

public class Skyblock extends SecretSeed {
    public Skyblock(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "skyblock".equals(seed) || "sky block".equals(seed);
    }
}
