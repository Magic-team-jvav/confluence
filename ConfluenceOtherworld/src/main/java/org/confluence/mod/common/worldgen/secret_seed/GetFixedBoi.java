package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.resources.ResourceLocation;

public class GetFixedBoi extends SecretSeed {
    public GetFixedBoi(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "getfixedboi".equals(seed) || "get fixed boi".equals(seed);
    }
}
