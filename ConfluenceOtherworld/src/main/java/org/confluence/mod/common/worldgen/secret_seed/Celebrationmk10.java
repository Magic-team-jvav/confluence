package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.resources.ResourceLocation;

public class Celebrationmk10 extends SecretSeed {
    public Celebrationmk10(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        try {
            return Long.parseLong(seed) == 5162011L || "celebrationmk10".equals(seed);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
